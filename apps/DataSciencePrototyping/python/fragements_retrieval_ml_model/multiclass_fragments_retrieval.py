'''
from __future__ import unicode_literals
from __future__ import print_function
import os
import sys
import json
import logging
import time
timestr = time.strftime("%Y%m%d-%H%M%S")

#Importing the configuration's from config.json
with open('config.json') as config_file:
    data = json.load(config_file)
    mlmodel_save_path = data['mlmodel_save_path']
    input_files = data['input_files']
    logpath = data['logs_save_path']
    train_dataset = data['train_data']
    test_dataset = data['test_data']

print("Program started multiclass_fragments.py %s"%(timestr))
logname = logpath+'ml_model_generator_'+timestr+'.log'
logging.basicConfig(filename=logname, filemode='w', format='%(asctime)s %(lineno)d %(name)s %(funcName)s %(levelname)s %(message)s',level=logging.INFO)
import pandas as pd
import numpy as np
from text_processing_lib import clean_text, get_n_grams
from sklearn.preprocessing import PolynomialFeatures
from sklearn.decomposition import PCA
import random
import pickle

from sklearn.neighbors import KNeighborsClassifier
from sklearn import metrics
from scipy.spatial.distance import cosine, minkowski
from sklearn import preprocessing
from sklearn.model_selection import StratifiedKFold

# Read the train and test datasets.
df = pd.read_csv(train_dataset)
df_TEST = pd.read_csv(test_dataset)
# As it was mentoioned above we have only 3 classes.
LABELS_MAP = {
    "NOTHING": 0,
    "FORCE MAJEURE": 1,
    "RIG REPAIR": 2,
}

def get_label(row):
#     if row.NPT_responsibility == True:
#         return LABELS_MAP[row.restriction]
#     return 0
    return LABELS_MAP[row.restriction]
    
df["preprocessed_text"] = [clean_text(text) for text in df.text.tolist()]
df["label"] = [get_label(row) for _, row in df.iterrows()]

df_TEST["preprocessed_text"] = [clean_text(text) for text in df_TEST.text.tolist()]
df_TEST["label"] = [get_label(row) for _, row in df_TEST.iterrows()]

print('Head of DF preprocessed_text: %s'%(df["preprocessed_text"].head(3)))
print('Head of DF label:  %s'%(df["label"].head(3)))

print('Head of DF TEST preprocessed_text: %s'%(df_TEST["preprocessed_text"].head(3)))
print('Head of DF TEST label:  %s'%(df_TEST["label"].head(3)))



df__ = pd.DataFrame({"#Force Majeure": [len(df[df.label == 1])],
                     "#Rig Repair": [len(df[df.label == 2])],
                     "#Nothing": [len(df[df.label == 0])]
                  }, columns=["#Force Majeure", "#Rig Repair", "#Nothing"])
df__["Positives fraction (%)"] = [100*float(len(df[df.label != 0])) / len(df)]

AUSTIN_UNIGRAMS = (
    "event", "forc", "majeur", "stoppag", "failur", "storm", "damag",
    "oblig", "repair", "loop", "downtim", "mainten", "hurrican", "weather"
)

AUSTIN_BIGRAMS = (
    "forc majeur", "majeur event", "drill unit",
    "mechan failur", "work stoppag", "loop current",
    "failur damag", "stoppag servic", "storm packer",
    "receiv compen", "majeur rate", "stoppag due",
    "damag drill", "dgd equip", "event forc", "repair mainten",
    "repair drill", "stoppag continu", "servic stoppag",
    "name storm", "equip downtim", "storm loop", "stoppag bank",
    "result disrupt", "delay failur", "tropic storm",
    "hurrican loop", "downtim forc",
)

RIG_REPAIR_STOPWORDS = [
    u'stoppag', u'recommenc', u'expir', u'stop', u'downtim',
    u'zero', u'return', u'damage', u'oblige', u'repair',
]

FORCE_MAJEURE_STOPWORDS = [
    u'event', u'majeur', u'forc', u'loop', u'weather', u'sea', u'storm', u'hurrican',
]

RESPONSIBILITY_BIGRAMS = [u'addit compens', u'carri oblig', u'compani oblig', u'compani pay',
                          u'compani termin', u'compens compani', u'compens period', u'document cost',
                          u'handl charg', u'oblig pay', u'pay money', u'payment oblig', u'penalti compani',
                          u'reason expens', u'shall advis', u'shall oblig', u'shall paid', 'shall pay',
                          u'shall receiv', u'shall requir', u'shall result', u'without penalti']

def remove_correlated_features(X_train, X_test):
    temp_df_train = pd.DataFrame(X_train)
    temp_df_test = pd.DataFrame(X_test)

    # Create correlation matrix
    corr_matrix = temp_df_train.corr().abs()

    # Select upper triangle of correlation matrix
    upper = corr_matrix.where(np.triu(np.ones(corr_matrix.shape), k=1).astype(np.bool))

    # Find index of feature columns with correlation greater than 0.95
    to_drop = [column for column in upper.columns if any(upper[column] > 0.95)]

    # Drop features 
    temp_df_train = temp_df_train.drop(temp_df_train.columns[to_drop], axis=1)
    temp_df_test = temp_df_test.drop(temp_df_test.columns[to_drop], axis=1)
    # Convert to Numpy arrays
    X_train_uncorr = temp_df_train.as_matrix()
    X_test_uncorr = temp_df_test.as_matrix()
    
    return X_train_uncorr, X_test_uncorr, to_drop

def create_new_features(texts):
    X = []
    for text in texts:
        text_features = []
        ngrams = get_n_grams(text)
        # RIG_REPAIR_STOPWORDS frequencies.
        for unigram in RIG_REPAIR_STOPWORDS:
             text_features.append(ngrams[1][unigram])
        # FORCE_MAJEURE_STOPWORDS frequencies.
        for unigram in FORCE_MAJEURE_STOPWORDS:
             text_features.append(ngrams[1][unigram])
        # Bigrams frequencies.
        for bigram in AUSTIN_BIGRAMS:
             text_features.append(ngrams[2][bigram])
        # Number of words.
        text_features.append(len(text.split()))
        # Occured uni- grams counts.
        text_features.append(sum([ngrams[1][unigram] for unigram in RIG_REPAIR_STOPWORDS]))
        text_features.append(sum([ngrams[1][unigram] for unigram in FORCE_MAJEURE_STOPWORDS]))
        
        # Responsibility bigrams.
        #text_features.append(sum([ngrams[2][unigram] for bigram in RESPONSIBILITY_BIGRAMS]))
        
        X.append(text_features)
        
    return np.array(X).astype(np.float64)
    
def generate_hc_features(train_df, test_df, return_scaler=False):
    X_train = create_new_features(train_df.preprocessed_text)
    X_test = create_new_features(test_df.preprocessed_text)

    return X_train, X_test

FOLDS = 5

skf = StratifiedKFold(n_splits=FOLDS)

K_fold_X_train = []
K_fold_X_dev = []

K_fold_y_train = []
K_fold_y_dev = []

for train_index, test_index in skf.split(df.text, df.restriction):
    train_df, dev_df = df.iloc[train_index], df.iloc[test_index]
    # Generate features for the both train and dev sets.
    X_train, X_dev = generate_hc_features(train_df, dev_df)
    y_train, y_dev = np.array(train_df.label.tolist()), np.array(dev_df.label.tolist())
    
    K_fold_X_train.append(X_train)
    K_fold_X_dev.append(X_dev)

    K_fold_y_train.append(y_train)
    K_fold_y_dev.append(y_dev)
    
print("Total number of features: {}".format(K_fold_X_train[0].shape[1]))

from sklearn.ensemble import RandomForestClassifier

wrong_results = []

average_score = 0

for fold, (train_index, dev_index) in enumerate(skf.split(df.text, df.restriction)):
    print("Fold {}".format(fold))

    rf_model = RandomForestClassifier(
        n_estimators=100, max_features=10, max_depth=8,
        random_state=107, class_weight="balanced", n_jobs=4)

    rf_model.fit(K_fold_X_train[fold], K_fold_y_train[fold])

    predicted = rf_model.predict(K_fold_X_dev[fold])

    score = metrics.f1_score(K_fold_y_dev[fold], predicted, average="macro")

    print("F1 averaged: %0.3f\n" % score)

    print("Confusion matrix: \n", metrics.confusion_matrix(K_fold_y_dev[fold], predicted), "\n")
    
    train_df, dev_df = df.iloc[train_index].reset_index(drop=True), df.iloc[dev_index].reset_index(drop=True)

    for i in range(len(predicted)):
        if predicted[i] != K_fold_y_dev[fold][i]:
            wrong_results.append((dev_df.iloc[i].text, K_fold_y_dev[fold][i], predicted[i]))
            
    #print_av_pr(metrics.confusion_matrix(K_fold_y_dev[fold], predicted))

    average_score += score

print("Averaged F1 score for all folds: {0:.3f}".format(float(average_score) / FOLDS))

# Generate features for the both train and test sets.
X_train, X_test = generate_hc_features(df, df_TEST, return_scaler=False)
y_train, y_test = np.array(df.label.tolist()), np.array(df_TEST.label.tolist())

rf_model = RandomForestClassifier(
                n_estimators=100, max_features=10, max_depth=8,
                random_state=107, class_weight="balanced", n_jobs=4)

rf_model.fit(X_train, y_train)

#rf_model = pickle.load(open("models/random_forest_model_v2.sav", 'rb'))

predicted = rf_model.predict(X_test)

predicted = rf_model.predict(X_train)
score = metrics.f1_score(y_train, predicted, average="macro")
print("F1 averaged: %0.3f\n" % score)
print("Confusion matrix: \n", metrics.confusion_matrix(y_train, predicted), "\n")

predicted = rf_model.predict(X_test)
score = metrics.f1_score(y_test, predicted, average="macro")
print("F1 averaged: %0.3f\n" % score)
print("Confusion matrix: \n", metrics.confusion_matrix(y_test, predicted), "\n")

# Save the model to disk.
filename = "random_forest_model_"+timestr+"_v2.sav"
pickle.dump(rf_model, open(mlmodel_save_path+filename, 'wb'))
# filename = "end_to_end_solution/scaler.sav"
# pickle.dump(scaler, open(filename, 'wb'))


for i in range(len(predicted)):
    if predicted[i] != 0 and y_test[i] == 0:
        print(i)
        print(df_TEST.iloc[i].text[:1000], "\n")

for i in range(len(predicted)):
    if predicted[i] == 0 and y_test[i] != 0:
        print(df_TEST.iloc[i].text[:1000], "\n")

'''