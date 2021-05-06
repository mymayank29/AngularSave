import pandas as pd
import pickle

# reads and returns pandas df stored in .pkl
def readPandasFromPickle(df_path, df_file_name):
    print("Reading pandas df from pickle, path: {}, file name: {}".format(df_path, df_file_name))
    pandas_df = pd.read_pickle(df_path + "/{}".format(df_file_name))
    print("...reading pandas df from pickle done!")
    return pandas_df

# reads and returns models stored in .pkl
def readModelFromPickle(model_path, model_name):
    print("Reading model from pickle, path: {}, model name: {}".format(model_path, model_name))
    model = pickle.load(open(model_path + "/{}".format(model_name), "rb"))
    print("..reading model from pickle done!")
    return model

# writes data (pandas df, models) to .pkl
def writePickle(data, output_path, file_name):
    createDirIfNotExist(output_path)
    print("Writing data to pickle, path: {}, file name: {}".format(output_path, file_name))
    pickle.dump(data, open(output_path + "/{}".format(file_name), "wb"))
    print("...writing data to pickle done!")

# reads parquet data and returns spark df
def readParquet(spark, df_path):
    print("Reading spark df from parquet, path {}".format(df_path))
    spark_df = spark.read.parquet(df_path)
    print("...reading spark df from parquet done!")
    return spark_df

# writes spark df to parquet
def writeParquet(spark_df, output_path):
    print("Writing spark_df to parquet, path {}".format(output_path))
    print("spark_df schema:")
    spark_df.printSchema()
    spark_df.write.mode('overwrite').parquet(output_path)
    print("...writing spark_df to parquet done!")


# creates directory if not exists
def createDirIfNotExist(path):
    import os
    if not os.path.exists(path):
        os.makedirs(path)
        print("dir created, path: {}".format(path))


def copyFile(file, source_path, target_path):
    from shutil import copyfile
    print("coping: {}".format(file))
    copyfile(source_path + "/" + file, target_path + "/" + file)
    print("coping done: {}".format(file))
