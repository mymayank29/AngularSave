package com.cvx.cdf.dwepcr.producer.ocr

import java.io.{BufferedOutputStream, FileInputStream}

import com.chevron.edap.common.text.parser.core.PdfOcrParser
import com.cvx.cdf.dwepcr.producer.service.{Config, SparkConnector}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, LocalFileSystem, Path}
import org.apache.hadoop.hdfs.DistributedFileSystem
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem
import org.apache.poi.xwpf.usermodel.{XWPFDocument, XWPFParagraph}

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.reflect.io.File

object ContractsOCRProcessor  extends Serializable {

  def main(args: Array[String]): Unit = {

    Config.appPath = args(0)
    Config.contractsSourcePath = args(1)
    val pathsToSrcFiles =  File(Config.contractsSourcePath).toDirectory.walk
      .map(entry => entry.path)
      .filter(path => !path.endsWith(".txt"))
      .filter(path => !path.endsWith(".rtf"))
      .filter(path => !path.endsWith(".msg"))
      .filter(path => !path.endsWith(".tif"))
      .filter(path => !path.endsWith(".xls"))
      .filter(path => !path.endsWith(".xlsx"))
      .filter(path => !path.endsWith(".dot"))
      .toSeq

    val pathsRDD = SparkConnector.sparkContext.parallelize(pathsToSrcFiles, 70)

    pathsRDD.foreach(path => {
      val destPath = new Path(Config.ocrResultPath + path.replace(Config.contractsSourcePath, "") + ".txt")
      @transient val configuration = configure()
      val fileSystem = FileSystem.newInstance(configuration)
      if(!fileSystem.exists(destPath)){
        val contentOfFile = processFile(path).mkString("\n")
        if(contentOfFile.length > 0) saveProcessedFileToHdfs(destPath, contentOfFile, fileSystem)
      }
    })
  }

  def processFile(pathToFile: String): ListBuffer[String] = {
    println("+++++++++ Processing file : " + pathToFile)
    var resultText = new ListBuffer[String]
    try{
      val fileToProcess =   File(pathToFile)
      if(fileToProcess.jfile.length() > 0) {
        val fileName = fileToProcess.name
        if(fileName.endsWith(".docx")) resultText ++= extractTextFromDocx(fileToProcess)
          else if (fileName.endsWith(".doc")) resultText ++= extractTextFromDoc(fileToProcess)
          else if (fileName.endsWith(".pdf")) resultText ++= extractTextFromPDF(fileToProcess)
          else println("Unable to process file with such extension : " + fileName)
      }
    } catch {
      case ex: Exception => println(ex)
    }
    resultText
  }

  def configure() : Configuration = {
    //    val conf = new Configuration(SparkConnector.sparkContext.hadoopConfiguration)
    val conf = new Configuration()
    conf.set("hdfs.use_datanode_hostname", "true")
    conf.addResource(new Path("/etc/hadoop/conf.cloudera.hdfs/core-site.xml"))
    conf.addResource(new Path("/etc/hadoop/conf.cloudera.hdfs/hdfs-site.xml"))
    conf.set("fs.hdfs.impl", classOf[DistributedFileSystem].getName())
    conf.set("fs.file.impl", classOf[LocalFileSystem].getName())
    conf
  }

  def saveProcessedFileToHdfs(path: Path, content : String, fileSystem: FileSystem): Unit = {
    println("+++++++++ Saving file to hdfs : " + path)
    val out = new BufferedOutputStream(fileSystem.create(path))
    out.write(content.getBytes("UTF-8"))
    out.flush()
    out.close()
    fileSystem.close()
  }

  def extractTextFromPDF(file: File): ListBuffer[String] = {
    val resultText = new ListBuffer[String]
    try{
      val doc = PDDocument.load(file.jfile)
      val parsedText =  new PDFTextStripper().getText(doc)
      if(!parsedText.trim().isEmpty) {
        println("+++++++++ Extracting already OCR-ed PDF file ...")
        resultText += parsedText
      } else {
        resultText ++= extractTextFromImagePDF(file)
      }
      doc.close()
    } catch {
      case ex: Exception => println(ex)
    }
    resultText
  }

  def extractTextFromImagePDF(file: File): ListBuffer[String] = {
    println("+++++++++ Extracting scanned PDF file ...")
    val resultText = new ListBuffer[String]
    try{
      val parser = new PdfOcrParser(file.path)
      //        FIXME: temp workaround as current version of common parser doesn't have public field 'DEBUG_DUMP_LEVEL'
      //        Field debugLevel = parser.getClass().getDeclaredField("DEBUG_DUMP_LEVEL");
      //        debugLevel.setAccessible(true);
      //        debugLevel.set(null, 0);

      //        FIXME: temp workaround as current version of common parser doesn't have public field 'TESSERACT_INIT_DATA_PATH'
      //        Field tessDataTrainPath = parser.getClass().getDeclaredField("TESSERACT_INIT_DATA_PATH");
      //        Field modifiersField = Field.class.getDeclaredField("modifiers");
      //        modifiersField.setAccessible(true);
      //        modifiersField.setInt(tessDataTrainPath, tessDataTrainPath.getModifiers() & ~Modifier.FINAL);
      //        tessDataTrainPath.setAccessible(true);
      //        tessDataTrainPath.set(null, tessTrainData);
      resultText ++= parser.parse().asScala
    } catch {
      case ex: Exception => println(ex)
    }
    resultText
  }

  def extractTextFromDocx(file: File): ListBuffer[String] = {
    println("+++++++++ Extracting DOCX file ...")
    var resultText = new ListBuffer[String]
    try{
      val fis = new FileInputStream(file.path)
      val document = new XWPFDocument(fis)
      resultText = document.getParagraphs.asScala.map(paragraph => paragraph.getText).to[ListBuffer]
      document.close()
      fis.close()
    } catch {
      case ex: Exception => println(ex)
    }
    resultText
  }

  def extractTextFromDoc(file: File): ListBuffer[String] = {
    println("+++++++++ Extracting DOC file ...")
    var resultText = new ListBuffer[String]
    try{
      val fs = new NPOIFSFileSystem(file.jfile)
      val extractor = new WordExtractor(fs.getRoot)
      resultText = extractor.getParagraphText.map(text => WordExtractor.stripFields(text)).to[ListBuffer]
      fs.close()
    } catch {
      case ex: Exception => println(ex)
    }
    resultText
  }

}
