from collections import defaultdict
from sklearn.ensemble import RandomForestClassifier
from sklearn.feature_extraction.text import TfidfTransformer, CountVectorizer
from sklearn.metrics import confusion_matrix, classification_report
from sklearn.pipeline import Pipeline

import glob2
import numpy as np
import pandas as pd
import tqdm

np.random.seed = 107

# NLTk imports
import nltk
from nltk.corpus import stopwords, wordnet
from nltk.stem.porter import PorterStemmer

stop_words = stopwords.words('english')

TRAIN_DATA_PATH = "Amendments_TrainData.xlsx"
TARGET_FOLDER = "Data/OCR_test"

AMENDMENTS_LOOKUP_PREFIX = 1000

DAT_PREFIX_KEY_WORDS = ("as of", "entered into effective",
                        "dated", "is made and", "dated")
NON_AMENDMENT_TITLE_KEY_WORDS = ("approval", "work_request")
NON_AMENDMENT_CONTENT_KEY_WORDS = (" you", " dear")

RESULT_EXCEL_REPORT_NAME = "Retrieved Amendments_contracts_test.xlsx"

class TextCleaner():
    """Simple text cleaner."""

    def __init__(self):
        self.stemmer = PorterStemmer()
        self.stop_words = stopwords.words('english')

    def _clean_text(self, text):
        try:
            text_ = text.decode("utf-8")
        except:
            text_ = text
        text = text_

        for ch in [",", "."]:
            text = text.replace(ch, "")

        # Lowercase text.
        text = text.lower()

        words = []
        for word in text.split():
            # Remove stop words.
            if not word.isalpha() or word in self.stop_words:
                continue
            # Stemming.
            word = self.stemmer.stem(word)
            words.append(word)

        return " ".join(words)

    def fit(self, raw_documents, y=None):
        self.fit_transform(raw_documents)
        return self

    def fit_transform(self, raw_documents, y=None):
        return np.array([self._clean_text(text) for text in raw_documents])

    def transform(self, raw_documents):
        return np.array([self._clean_text(text) for text in raw_documents])

def get_doc_content(filename, prefix_length=3000):
    content = ''
    with open(filename, "r") as file:
        for line in file:
            content += line.decode('utf-8', 'ignore').strip() + " "

    # Remove sequential spaces.
    content = " ".join([word for word in content.split() if word != ""])
    # Return only the first 'prefix_length' characters.
    return content[:prefix_length]

# Get documents contents
def get_docs_content(X):
    return np.array([get_doc_content(filename)
                     for filename in X])

def get_date_prefix(text):
    """Returns a prefix for a given string which may contain a date."""
    digit_groups = 0
    is_digit = 0
    for index, ch in enumerate(text):
        if is_digit == ch.isdigit():
            continue
        if not is_digit:
            digit_groups += 1
        else:
            if digit_groups == 2:
                return text[:index+1]
        is_digit = ch.isdigit()
    return text

if __name__ == "__main__":
    # Train dataset.
    amendments_df = pd.read_excel(TRAIN_DATA_PATH)
    # Files paths and a set of labels.
    X, y = amendments_df["filename"].values, \
           amendments_df["Amendment?"].values.astype(int)
    print "Train set shape: {}".format(X.shape)
    ############################################################################
    print "\n\n\nVectorizing the train data..."

    # Read files.
    X = get_docs_content(X)
    # Clean the data and transform texts into TF-IDF representations.
    text_transformer = Pipeline([
        ('clean', TextCleaner()),
        ('vect', CountVectorizer()),
        ('tfidf', TfidfTransformer())
    ])
    X = text_transformer.fit_transform(X)
    print "Train set shape after transformation: {}".format(X.shape)
    ############################################################################
    print "\n\n\nFitting the model..."

    # Fit RandomForest model.
    rf_clf = RandomForestClassifier(n_estimators=40,
                                    max_depth=5,
                                    max_features=20,
                                    random_state=107)
    rf_clf.fit(X, y)
    ############################################################################
    # Evaluate the model on the train data.
    print "\n\n\nIn-sample evaluation:"

    y_pred = rf_clf.predict(X)

    print "Confusion matrix: \n", confusion_matrix(y, y_pred), "\n"
    print classification_report(y, y_pred,
                                target_names=["Other files", "Amendments"])
    ############################################################################

    ##############################
    # Unseen amendments retrieval.
    ##############################
    print "Retrieving amendments from the target folder..."

    ocred_files = [filename
                   for filename in glob2.iglob("{}/**/*.txt".format(TARGET_FOLDER),
                                               recursive=True)]
    print "Total number of files: {}".format(len(ocred_files))
    ############################################################################
    print "\n\n\nFilter out the most obvious candidates..."

    results = defaultdict(list)
    for filename in tqdm.tqdm(ocred_files):
        try:
            # Read a document.
            text = get_doc_content(filename)

            # Check if there are any "amendment" occurrences within file content.
            if "amendment" not in text[:AMENDMENTS_LOOKUP_PREFIX].lower():
                continue

            # Use the model to check whether it is an amendment or not.
            is_amendment = rf_clf.predict(text_transformer.transform([text]))

            if is_amendment:
                results["Contract"].append(filename.split("\\")[1])
                results["Contract Prefix"].append(text[:1000].lower())
                results["Relative File Path"].append(
                    "\\".join(filename.split("\\")[1:]))
                results["Text_length"].append(len(text))
        except:
            print "Error: {}".format(filename)

    results = pd.DataFrame(results)
    print "The result number of amendments: {}".format(results.shape)
    ############################################################################
    print "\n\n\nDates retrieval and additional filtering..."

    correct_amendment_ids = []
    retrieved_dates = []

    dated_files_count = 0
    not_dated_files_count = 0

    for index, row in results.iterrows():
        text = row["Contract Prefix"]
        title = row["Relative File Path"].lower()

        ########################################################################
        # Check: if date is presented within title
        # Example:
        #   Title: 09__On_Site_Network_and_Telecommunications_Support_(01Jan14).pdf.txt
        #  => date_candidate = 01Jan14
        date_candidate = title.split("(")[-1].split(")")[0].replace("_", "")

        if date_candidate[-2:].isdigit() and \
                date_candidate[-5:-2].isalpha() and \
                date_candidate[:-5].isdigit():
            dated_files_count += 1

            correct_amendment_ids.append(index)
            retrieved_dates.append(date_candidate)
            continue
        ########################################################################

        for key_word in DAT_PREFIX_KEY_WORDS:
            if key_word in text:
                dated_files_count += 1

                date_candidate = text.split(key_word)[1][:50]
                # Remove trash from the beginning of the string.
                for prefix in [" ", "effective", " "]:
                    if date_candidate.startswith(prefix):
                        date_candidate = date_candidate[len(prefix):]
                # Retrieve date.
                date_candidate = get_date_prefix(date_candidate)

                correct_amendment_ids.append(index)
                retrieved_dates.append(date_candidate)
                break
        else:
            ####################################################################
            # Check: whether it is an amendment or not.
            is_amendment = True
            # Check using title.
            for key_word in NON_AMENDMENT_TITLE_KEY_WORDS:
                if key_word in title.split("\\")[-1]:
                    is_amendment = False

            # Check using content.
            for key_word in NON_AMENDMENT_CONTENT_KEY_WORDS:
                if key_word in text:
                    is_amendment = False

            if not is_amendment:
                continue
            ####################################################################

            not_dated_files_count += 1
            # At that moment we don't really know whether the current files is
            # an amendment or not, just assuming.
            correct_amendment_ids.append(index)
            retrieved_dates.append("Unknown")

    print "The total number of dated files: {}".format(dated_files_count)
    print "The total number of undated files: {}".format(not_dated_files_count)

    # Populate the result dataframe with the findings.
    results = results.iloc[correct_amendment_ids].reset_index(drop=True)
    results["Date"] = retrieved_dates

    # Save the results.
    results.to_excel(RESULT_EXCEL_REPORT_NAME, index=False)
