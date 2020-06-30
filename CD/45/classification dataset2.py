#!/usr/bin/env python
# coding: utf-8

# ### Imports

# In[2]:


import warnings
warnings.filterwarnings('ignore')
from sklearn import neighbors, model_selection, metrics
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
from sklearn.naive_bayes import GaussianNB, MultinomialNB, BernoulliNB
from sklearn.preprocessing import label_binarize
from sklearn import preprocessing
from sklearn import tree
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import KBinsDiscretizer
from sklearn.linear_model import LinearRegression
import statistics
import graphviz
from sklearn.feature_selection import chi2
from sklearn.feature_selection import SelectKBest
from subprocess import check_call
import os
from sklearn.feature_selection import VarianceThreshold
from sklearn.naive_bayes import GaussianNB, MultinomialNB, BernoulliNB
from sklearn.metrics import confusion_matrix, precision_recall_curve, auc, roc_auc_score, roc_curve, recall_score, classification_report
import matplotlib.patches as mpatches
from pylab import rcParams
from sklearn.preprocessing import MinMaxScaler
from collections import Counter
from imblearn.over_sampling import SMOTE
from imblearn.under_sampling import RandomUnderSampler
from sklearn.model_selection import train_test_split
import itertools


# ### Aux. functions

# In[3]:


def compute_metrics(confusion_matrix):
    #https://stackoverflow.com/questions/31324218/scikit-learn-how-to-obtain-true-positive-true-negative-false-positive-and-fal
    FP = confusion_matrix.sum(axis=0) - np.diag(confusion_matrix)
    FN = confusion_matrix.sum(axis=1) - np.diag(confusion_matrix)
    TP = np.diag(confusion_matrix)
    TN = confusion_matrix.sum() - (FP + FN + TP)

    # Sensitivity, hit rate, recall, or true positive rate
    TPR = TP / (TP + FN)

    # Specificity or true negative rate
    TNR = TN / (TN + FP)

    # Overall accuracy
    ACC = (TP + TN) / (TP + FP + FN + TN)
    
    cost = FP * 10 + FN * 500
    #Specific to this example
    return TPR,TNR, cost

def preprocessData(df):
    label_encoder = preprocessing.LabelEncoder()
    dummy_encoder = preprocessing.OneHotEncoder()
    pdf = pd.DataFrame()
    for att in df.columns:
        if df[att].dtype == np.float64 or df[att].dtype == np.int64:
            pdf = pd.concat([pdf, df[att]], axis=1)
        else:
            df[att] = label_encoder.fit_transform(df[att])
            # Fitting One Hot Encoding on train data
            temp = dummy_encoder.fit_transform(df[att].values.reshape(-1,1)).toarray()
            # Changing encoded features into a dataframe with new column names
            temp = pd.DataFrame(temp,
                                columns=[(att + "_" + str(i)) for i in df[att].value_counts().index])
            # In side by side concatenation index values should be same
            # Setting the index values similar to the data frame
            temp = temp.set_index(df.index.values)
            # adding the new One Hot Encoded varibales to the dataframe
            pdf = pd.concat([pdf, temp], axis=1)
    return pdf

def fill_missing_values(dataset, feature_names, class_name = None):
    dataset_preprocessed = dataset.copy()
    
    if class_name == None:
        for feature in feature_names:
            dataset_preprocessed[feature] = dataset_preprocessed.transform(lambda x: x.fillna(x.mean()))[feature]
    else:
        for feature in feature_names:
            dataset_preprocessed[feature] = dataset_preprocessed.groupby(class_name).transform(lambda x: x.fillna(x.mean()))[feature]

    return dataset_preprocessed


# # First dataset

# ## Load data

# In[4]:


green_ds = pd.read_csv("datasets/second/green.csv")
hinselmann_ds = pd.read_csv("datasets/second/hinselmann.csv")
schiller_ds = pd.read_csv("datasets/second/schiller.csv")

cols = green_ds.columns[:-7]
'''
green_ds["green_ds"] = 1
green_ds["hinselmann_ds"] = 0
green_ds["schiller_ds"] = 0

hinselmann_ds["green_ds"] = 0
hinselmann_ds["hinselmann_ds"] = 1
hinselmann_ds["schiller_ds"] = 0

schiller_ds["green_ds"] = 0
schiller_ds["hinselmann_ds"] = 0
schiller_ds["schiller_ds"] = 1
'''

ds = pd.concat([green_ds,hinselmann_ds,schiller_ds])

print(cols.shape)

ds.info()
ds.describe()
ds.corr()


# In[5]:


class_attributes = [
    "consensus",
    "experts::0",
    "experts::1",
    "experts::2",
    "experts::3",
    "experts::4",
    "experts::5",
    ]

X = ds.drop(labels = class_attributes, axis = "columns")
feature_names = X.columns.tolist()

Y = ds[class_attributes[0]]
unique, counts = np.unique(Y, return_counts=True)
ocorrences = dict(zip(unique, counts))
labels = pd.unique(Y)
Y = label_binarize(Y, classes=labels).reshape(-1)
positives = ds.loc[ds[class_attributes[0]] == 1.0]

X.shape


# # Pre-processing 1

# In[6]:


def normalize (dataset, feature_names, class_attributes):
    dataset_x = dataset.drop(labels = class_attributes, axis = "columns")
    dataset_y = dataset[class_attributes[0]]
    X_norm = preprocessing.normalize(dataset_x)
    preproc_1 = pd.DataFrame(data=X_norm, columns = feature_names)
    #preproc_1.insert(len(feature_names), class_attributes[0], dataset_y)
    return preproc_1


# In[7]:


result_pre1 = normalize(ds, feature_names, class_attributes)

X_preproc_1 = result_pre1
X_preproc_1.shape


# # Pre-processing 3

# In[8]:


def preproc_3(X, y):
    sm = SMOTE(random_state=2)
    X_sm, y_sm = sm.fit_sample(X, y.ravel())

    preproc_3 = pd.DataFrame(data=X_sm, columns = feature_names)
    
    return preproc_3, y_sm


# In[9]:


X_preproc_3, Y_preproc_3 = preproc_3(X, Y)

Y_preproc_3.shape
#Y_preproc_3.tolist()


# # Pre-processing 4

# In[10]:


def preproc_4 (dataset_filled_mv,y_train_filled_mv, num_cols, cols):
    
    X_train_filled_mv = dataset_filled_mv
    X_train_mv_norm = preprocessing.normalize(X_train_filled_mv)
    
    k = len(X_train_filled_mv.columns)
    selector = SelectKBest(score_func=chi2, k=k - num_cols)
    selector.fit(X_train_mv_norm, y_train_filled_mv)
    idxs_selected = selector.get_support(indices=True)
    
    columns = X_train_mv_norm[0][idxs_selected]
    features_dataframe_new = X_train_mv_norm[:,idxs_selected]
    
    #cols = dataset_filled_mv.iloc[:,1:].columns[idxs_selected]
    preproc_4 = pd.DataFrame(data = features_dataframe_new, columns=cols[idxs_selected])

    return preproc_4


# In[11]:


X_no_negatives = X
for attr in X:
    X_no_negatives[attr] = ds.transform(lambda x: x + abs(x.min()))[attr]
X_no_negatives.shape


# In[12]:


result_pre4 = preproc_4(X_no_negatives,Y, 10, cols)
X_preproc_4 = result_pre4
#Y_preproc_4 = label_binarize(Y_preproc_4, classes=Y_preproc_4.unique())
X_preproc_4.shape


# # ------------------------------------------------- KNN ---------------------------------------------------

# In[13]:


n_splits = 5
kf = model_selection.KFold(n_splits = n_splits)
Ks = [1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,30]

accurracies_knn, accurracies_knn_pre1, accurracies_knn_pre3, accurracies_knn_pre4 = [], [], [], []
costs_knn, costs_knn_pre1, costs_knn_pre3, costs_knn_pre4 = [], [], [], []
tpr_knn, tpr_knn_pre1, tpr_knn_pre3, tpr_knn_pre4 = [], [], [], []
fpr_knn, fpr_knn_pre1, fpr_knn_pre3, fpr_knn_pre4 = [], [], [], []

X_train0, X_test0, Y_train0, Y_test0 = model_selection.train_test_split(X,Y, train_size = 0.7, test_size = 0.3, stratify = Y)
X_train1, X_test1, Y_train1, Y_test1 = model_selection.train_test_split(X_preproc_1,Y, train_size = 0.7, test_size = 0.3, stratify = Y)
X_train3, X_test3, Y_train3, Y_test3 = model_selection.train_test_split(X_preproc_3,Y_preproc_3, train_size = 0.7, test_size = 0.3, stratify = Y_preproc_3)
X_train4, X_test4, Y_train4, Y_test4 = model_selection.train_test_split(X_preproc_4,Y, train_size = 0.7, test_size = 0.3, stratify = Y)

for k in Ks:
    print("K =",k)
    knn = neighbors.KNeighborsClassifier(n_neighbors = k)
    
    print("___1___")
    model = knn.fit(X_train0,Y_train0)
    predY = model.predict(X_test0)

    cm = metrics.confusion_matrix(Y_test0, predY,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test0, Y_test0)
    fpr, tpr, thresholds = roc_curve(Y_test0, predY)
    accurracies_knn.append(accurracy)
    costs_knn.append(cost[0])
    tpr_knn.append(tpr)
    fpr_knn.append(fpr)
    print(cm)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    print("Acurracy: ", accurracy)
    
    print("___1___")
    
    model = knn.fit(X_train1,Y_train1)
    predY = model.predict(X_test1)

    cm = metrics.confusion_matrix(Y_test1, predY,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test1, Y_test1)
    fpr, tpr, thresholds = roc_curve(Y_test1, predY)
    accurracies_knn_pre1.append(accurracy)
    costs_knn_pre1.append(cost[0])
    tpr_knn_pre1.append(tpr)
    fpr_knn_pre1.append(fpr)
    print(cm)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    print("Acurracy: ", accurracy)
    
    print("___3___") 
    
    model = knn.fit(X_train3,Y_train3)
    predY = model.predict(X_test3)

    cm = metrics.confusion_matrix(Y_test3, predY,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test3, Y_test3)
    fpr, tpr, thresholds = roc_curve(Y_test3, predY)
    accurracies_knn_pre3.append(accurracy)
    costs_knn_pre3.append(cost[0])
    tpr_knn_pre3.append(tpr)
    fpr_knn_pre3.append(fpr)
    print(cm)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    print("Acurracy: ", accurracy)
    
    print("___4___") 
    
    model = knn.fit(X_train4,Y_train4)
    predY = model.predict(X_test4)

    cm = metrics.confusion_matrix(Y_test4, predY,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test4, Y_test4)
    fpr, tpr, thresholds = roc_curve(Y_test4, predY)
    accurracies_knn_pre4.append(accurracy)
    costs_knn_pre4.append(cost[0])
    tpr_knn_pre4.append(tpr)
    fpr_knn_pre4.append(fpr)
    print(cm)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    print("Acurracy: ", accurracy)
    
    print("--------------------------------------------------------------")


# In[14]:


print(tpr_knn)
print(fpr_knn)

roc_aucs_knn_baseline = []
for i in range(0,len(tpr_knn)):
    #roc_aucs_knn_baseline.append(auc(fpr[i],tpr[i]))
    plt.plot(fpr_knn[i], tpr_knn[i], 'b',label='AUC = %0.3f'% auc(fpr_knn[i],tpr_knn[i]))

plt.legend(loc='lower right')
plt.plot([0,1],[0,1],'r--')
plt.xlim([-0.1,1.0])
plt.ylim([-0.1,1.01])
plt.ylabel('True Positive Rate')
plt.xlabel('False Positive Rate')
plt.title("Roc curve (baseline)")
plt.show()


# In[15]:


print(tpr_knn_pre1)
print(fpr_knn_pre1)

#roc_aucs_knn_baseline = []
for i in range(0,len(tpr_knn)):
    #roc_aucs_knn_baseline.append(auc(fpr[i],tpr[i]))
    plt.plot(fpr_knn_pre1[i], tpr_knn_pre1[i], 'b',label='AUC = %0.3f'% auc(fpr_knn_pre1[i],tpr_knn_pre1[i]))

plt.legend(loc='lower right')
plt.plot([0,1],[0,1],'r--')
plt.xlim([-0.1,1.0])
plt.ylim([-0.1,1.01])
plt.ylabel('True Positive Rate')
plt.xlabel('False Positive Rate')
plt.title("Roc curve (pre-proc1)")
plt.show()


# In[15]:


rcParams['figure.figsize'] = 10, 7

#print(accurracies_knn)
#print(accurracies_knn_pre1)
#print(accurracies_knn_pre3)
#print(accurracies_knn_pre4)

Ks = [1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,30]

base_knn_acc = plt.plot(Ks, accurracies_knn,'m', label='baseline')
#pre1_knn_acc = plt.plot(Ks, accurracies_knn_pre1,'c', label='pre-proc1')
#pre2_knn_acc = plt.plot(Ks, accurracies_knn_pre3,'y', label='pre-proc3')
pre2_knn_acc = plt.plot(Ks, accurracies_knn_pre4,'tomato', label='pre-proc4')
plt.ylabel("Accuracy")
plt.xlabel("K")
plt.xticks(np.arange(1, 21, step=2))
plt.yticks(np.arange(0.5, 1, step=0.1))
plt.legend()
plt.show()


# In[17]:


print(tpr_knn_pre4)
print(fpr_knn_pre4)

for i in range(0,len(fpr_knn_pre4)):
    plt.plot(fpr_knn_pre4[i], tpr_knn_pre4[i], 'b',label='AUC = %0.3f'% auc(fpr_knn_pre4[i],tpr_knn_pre4[i]))

plt.legend(loc='lower right')
plt.plot([0,1],[0,1],'r--')
plt.xlim([-0.1,1.0])
plt.ylim([-0.1,1.01])
plt.ylabel('True Positive Rate')
plt.xlabel('False Positive Rate')
plt.title("Roc curve (pre-proc2)")
plt.show()


# In[18]:


print(tpr_knn_pre3)
print(fpr_knn_pre3)

for i in range(0,len(fpr_knn_pre3)):
    plt.plot(fpr_knn_pre3[i], tpr_knn_pre3[i], 'b',label='AUC = %0.3f'% auc(fpr_knn_pre3[i],tpr_knn_pre3[i]))

plt.legend(loc='lower right')
plt.plot([0,1],[0,1],'r--')
plt.xlim([-0.1,1.0])
plt.ylim([-0.1,1.01])
plt.ylabel('True Positive Rate')
plt.xlabel('False Positive Rate')
plt.title("Roc curve (pre-proc3)")
plt.show()


# # ------------------------------------------- Naive Bayes -------------------------------------

# In[17]:


gnb = GaussianNB()

accurracy_NB_baseline, accurracy_NB_pre1, accurracy_NB_pre3, accurracy_NB_pre4 = [],[],[],[]
sensitivity_NB_baseline, sensitivity_NB_pre1, sensitivity_NB_pre3, sensitivity_NB_pre4 = [],[],[],[]
cost_NB_baseline, cost_NB_pre1, cost_NB_pre3, cost_NB_pre4 = [],[],[],[]

kf = model_selection.KFold(n_splits = 10)
print("--- BASELINE ---")
for train_index, test_index in kf.split(X,Y):
    X_train, X_test = X.iloc[train_index], X.iloc[test_index]
    Y_train, Y_test = Y[train_index], Y[test_index]
    
    model = gnb.fit(X_train,Y_train)
    predYNB = model.predict(X_test)
    cm = metrics.confusion_matrix(Y_test, predYNB,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test, Y_test)
    sensitivity = Sens[0]
    cost = cost[0]
    
    accurracy_NB_baseline.append(accurracy)
    sensitivity_NB_baseline.append(sensitivity)
    cost_NB_baseline.append(cost)
    
    print(cm)
    print("Sensitivity NB Baseline:", sensitivity)
    print("Specificity NB Baseline:", Spec[0])
    print("Cost NB Baseline:", cost)
    print("Acurracy NB Baseline:", accurracy)

print("--- PRE-1 ---")
for train_index, test_index in kf.split(X_preproc_1,Y):
    X_train, X_test = X_preproc_1.iloc[train_index], X_preproc_1.iloc[test_index]
    Y_train, Y_test = Y[train_index], Y[test_index]
    
    model = gnb.fit(X_train,Y_train)
    predYNB = model.predict(X_test)
    cm = metrics.confusion_matrix(Y_test, predYNB,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test, Y_test)
    sensitivity = Sens[0]
    cost = cost[0]
    
    accurracy_NB_pre1.append(accurracy)
    sensitivity_NB_pre1.append(sensitivity)
    cost_NB_pre1.append(cost)
    
    print(cm)
    print("Sensitivity NB Baseline:", sensitivity)
    print("Specificity NB Baseline:", Spec[0])
    print("Cost NB Baseline:", cost)
    print("Acurracy NB Baseline:", accurracy)
    
print("--- PRE-3 ---")
for train_index, test_index in kf.split(X_preproc_3,Y_preproc_3):
    X_train, X_test = X_preproc_3.iloc[train_index], X_preproc_3.iloc[test_index]
    Y_train, Y_test = Y_preproc_3[train_index], Y_preproc_3[test_index]
    
    model = gnb.fit(X_train,Y_train)
    predYNB = model.predict(X_test)
    cm = metrics.confusion_matrix(Y_test, predYNB,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test, Y_test)
    sensitivity = Sens[0]
    cost = cost[0]
    
    accurracy_NB_pre3.append(accurracy)
    sensitivity_NB_pre3.append(sensitivity)
    cost_NB_pre3.append(cost)
    
    print(cm)
    print("Sensitivity NB Baseline:", sensitivity)
    print("Specificity NB Baseline:", Spec[0])
    print("Cost NB Baseline:", cost)
    print("Acurracy NB Baseline:", accurracy)
    
print("--- PRE-4 ---")
for train_index, test_index in kf.split(X_preproc_4,Y):
    X_train, X_test = X_preproc_4.iloc[train_index], X_preproc_4.iloc[test_index]
    Y_train, Y_test = Y[train_index], Y[test_index]
    
    model = gnb.fit(X_train,Y_train)
    predYNB = model.predict(X_test)
    cm = metrics.confusion_matrix(Y_test, predYNB,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test, Y_test)
    sensitivity = Sens[0]
    cost = cost[0]
    
    accurracy_NB_pre4.append(accurracy)
    sensitivity_NB_pre4.append(sensitivity)
    cost_NB_pre4.append(cost)
    
    print(cm)
    print("Sensitivity NB Baseline:", sensitivity)
    print("Specificity NB Baseline:", Spec[0])
    print("Cost NB Baseline:", cost)
    print("Acurracy NB Baseline:", accurracy)

print("------------------------------------------------------------------------------------------")
    
print("Sensitivity NB Baseline:", np.mean(sensitivity_NB_baseline))
print("Cost NB Baseline:", np.mean(cost_NB_baseline))
print("Acurracy NB Baseline:", np.mean(accurracy_NB_baseline))
print('\n')
print("Sensitivity NB Pre1:", np.mean(sensitivity_NB_pre1))
print("Cost NB Pre1:", np.mean(cost_NB_pre1))
print("Acurracy NB Pre1:", np.mean(accurracy_NB_pre1))
print('\n')
print("Sensitivity NB Pre3:", np.mean(sensitivity_NB_pre3))
print("Cost NB Pre3:", np.mean(cost_NB_pre3))
print("Acurracy NB Pre3:", np.mean(accurracy_NB_pre3))
print('\n')
print("Sensitivity NB Pre4:", np.mean(sensitivity_NB_pre4))
print("Cost NB Pre4:", np.mean(cost_NB_pre4))
print("Acurracy NB Pre4:", np.mean(accurracy_NB_pre4))

mean_accuracy_NB_baseline = np.mean(accurracy_NB_baseline)
mean_accuracy_NB_pre1 = np.mean(accurracy_NB_pre1)
mean_accuracy_NB_pre3 = np.mean(accurracy_NB_pre3)
mean_accuracy_NB_pre4 = np.mean(accurracy_NB_pre4)

mean_cost_NB_baseline = np.mean(cost_NB_baseline)
mean_cost_NB_pre1 = np.mean(cost_NB_pre1)
mean_cost_NB_pre3 = np.mean(cost_NB_pre3)
mean_cost_NB_pre4 = np.mean(cost_NB_pre4)

mean_sensitivity_NB_baseline = np.mean(sensitivity_NB_baseline)
mean_sensitivity_NB_pre1 = np.mean(sensitivity_NB_pre1)
mean_sensitivity_NB_pre3 = np.mean(sensitivity_NB_pre3)
mean_sensitivity_NB_pre4 = np.mean(sensitivity_NB_pre4)


# In[18]:


plt.figure()
#plt.bar(["baseline","pre-proc1","pre-proc3","pre-proc4"],[mean_accuracy_NB_baseline,mean_accuracy_NB_pre1,mean_accuracy_NB_pre3,mean_accuracy_NB_pre4])
plt.bar(["baseline","pre-proc4"],[mean_accuracy_NB_baseline,mean_accuracy_NB_pre4])
plt.show()


# # C4.5 (decision tree)
# 

# varying max_depth

# In[19]:


depths = range(1,21)

accurracies_DT_depth = []
costs_DT_depth = []
sensitivities_DT_depth = []

accurracies_DT_depth_pre4= []
costs_DT_depth_pre4 = []
sensitivities_DT_depth_pre4= []

X_train0, X_test0, Y_train0, Y_test0 = model_selection.train_test_split(X,Y, train_size = 0.7, test_size = 0.3, stratify = Y)
X_train4, X_test4, Y_train4, Y_test4 = model_selection.train_test_split(X_preproc_4,Y, train_size = 0.7, test_size = 0.3, stratify = Y)
for depth in depths:
    
    
    print("max_depth =", depth)
    clf = tree.DecisionTreeClassifier(max_depth=depth)
    
    model = clf.fit(X_train0, Y_train0)
    predYClf = model.predict(X_test0)
    cmClf = metrics.confusion_matrix(Y_test0, predYClf,labels=[1, 0])
    accuracy = model.score(X_test0, Y_test0)
    Sens, Spec, cost = compute_metrics(cmClf)
    accurracies_DT_depth.append(accuracy)
    costs_DT_depth.append(cost[0])
    sensitivities_DT_depth.append(Sens[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
     
    print("---4---")
    model = clf.fit(X_train4, Y_train4)
    predYClf = model.predict(X_test4)
    cmClf = metrics.confusion_matrix(Y_test4, predYClf,labels=[1, 0])
    accuracy = model.score(X_test4, Y_test4)
    Sens, Spec, cost = compute_metrics(cmClf)
    accurracies_DT_depth_pre4.append(accuracy)
    costs_DT_depth_pre4.append(cost[0])
    sensitivities_DT_depth_pre4.append(Sens[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    print("--------------------------------------------------------------")
    
print("Accuracies baseline: ", accurracies_DT_depth)
print("Costs baseline: ", costs_DT_depth)
print("Sensitivities baseline: ", sensitivities_DT_depth)

print("Accuracies pre4: ", accurracies_DT_depth_pre4)
print("Costs pre4: ", costs_DT_depth_pre4)
print("Sensitivities pre4: ", sensitivities_DT_depth_pre4)


# In[95]:


Ks = range(1, 21)

plt.figure()
plt.title('Decision Tree - Accuracy')
plt.plot(Ks, accurracies_DT_depth,'m', label='baseline')
plt.plot(Ks, accurracies_DT_depth_pre4,'c', label='pre-proc4')
plt.legend()
plt.xlabel("max depth")
plt.ylabel("accuracy")
plt.xticks(np.arange(1, 21, step=2))
plt.yticks(np.arange(0.5, 1, step=0.1))
plt.show()

plt.figure()
plt.title('Decision Tree - Sensitivity')
plt.plot(Ks, sensitivities_DT_depth,'m', label='baseline')
plt.plot(Ks, sensitivities_DT_depth_pre4,'c', label='pre-proc4')
plt.xlabel("max depth")
plt.ylabel("sensitivity")
plt.xticks(np.arange(1, 21, step=2))
plt.yticks(np.arange(0.5, 1, step=0.1))
plt.legend()
plt.show()


# In[102]:


splits = range(2,21,2)

accurracies_DT_poda0 = []
costs_DT_poda0 = []
sensitivities_DT_poda0 = []

accurracies_DT_poda4 = []
costs_DT_poda4 = []
sensitivities_DT_poda4 = []

accurracies_DT_0 = []
costs_DT_0 = []
sensitivities_DT_0 = []

accurracies_DT_4 = []
costs_DT_4 = []
sensitivities_DT_4 = []

X_train0, X_test0, Y_train0, Y_test0 = model_selection.train_test_split(X,Y, train_size = 0.7, test_size = 0.3, stratify = Y)    
X_train4, X_test4, Y_train4, Y_test4 = model_selection.train_test_split(X_preproc_4,Y, train_size = 0.7, test_size = 0.3, stratify = Y)
for split in splits:
    print(split)
    
    print("---0 s/poda---")
    clf = tree.DecisionTreeClassifier(min_samples_split=split)
    model = clf.fit(X_train0, Y_train0)
    predYClf = model.predict(X_test0)
    cmClf = metrics.confusion_matrix(Y_test0, predYClf,labels=[1, 0])
    accuracy = model.score(X_test0, Y_test0)
    Sens, Spec, cost = compute_metrics(cmClf)
    accurracies_DT_0.append(accuracy)
    costs_DT_0.append(cost[0])
    sensitivities_DT_0.append(Sens[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    
    print("---0 c/poda---")
    clf = tree.DecisionTreeClassifier(max_depth=8,min_samples_split=split)
    model = clf.fit(X_train0, Y_train0)
    predYClf = model.predict(X_test0)
    cmClf = metrics.confusion_matrix(Y_test0, predYClf,labels=[1, 0])
    accuracy = model.score(X_test0, Y_test0)
    Sens, Spec, cost = compute_metrics(cmClf)
    accurracies_DT_poda0.append(accuracy)
    costs_DT_poda0.append(cost[0])
    sensitivities_DT_poda0.append(Sens[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    
    print("---4 s/poda---")
    clf = tree.DecisionTreeClassifier(min_samples_split=split)
    model = clf.fit(X_train4, Y_train4)
    predYClf = model.predict(X_test4)
    cmClf = metrics.confusion_matrix(Y_test4, predYClf,labels=[1, 0])
    accuracy = model.score(X_test4, Y_test4)
    Sens, Spec, cost = compute_metrics(cmClf)
    accurracies_DT_4.append(accuracy)
    costs_DT_4.append(cost[0])
    sensitivities_DT_4.append(Sens[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    
    print("---4 c/poda---")
    
    clf = tree.DecisionTreeClassifier(max_depth=8,min_samples_split=split)
    model = clf.fit(X_train4, Y_train4)
    predYClf = model.predict(X_test4)
    cmClf = metrics.confusion_matrix(Y_test4, predYClf,labels=[1, 0])
    accuracy = model.score(X_test4, Y_test4)
    Sens, Spec, cost = compute_metrics(cmClf)
    accurracies_DT_poda4.append(accuracy)
    costs_DT_poda4.append(cost[0])
    sensitivities_DT_poda4.append(Sens[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    
    print("--------------------------------------------------------------")
print("Accuracies 0: ", accurracies_DT_0)
print("Costs 0: ", costs_DT_0)
print("Sensitivities 0: ", sensitivities_DT_0)

print("Accuracies 4: ", accurracies_DT_4)
print("Costs 4: ", costs_DT_4)
print("Sensitivities 4: ", sensitivities_DT_4)

print("Accuracies poda0: ", accurracies_DT_poda0)
print("Costs poda0: ", costs_DT_poda0)
print("Sensitivities poda0: ", sensitivities_DT_poda0)

print("Accuracies poda4: ", accurracies_DT_poda4)
print("Costs poda4: ", costs_DT_poda4)
print("Sensitivities poda4: ", sensitivities_DT_poda4)


# In[103]:


Ks = range(2,21,2)

plt.figure()
plt.title('Decision Tree - Accuracy')
plt.plot(Ks, accurracies_DT_0,'m', label='baseline')
plt.plot(Ks, accurracies_DT_4,'coral', label='pre-proc4')
plt.plot(Ks, accurracies_DT_poda0,'y', label='baseline w/ prune')
plt.plot(Ks, accurracies_DT_poda4,'greenyellow', label='pre-proc4 w/ prune')
plt.legend()
plt.xlabel("max depth")
plt.ylabel("accuracy")
plt.xticks(np.arange(1, 21, step=2))
plt.yticks(np.arange(0.5, 1, step=0.1))
plt.show()

plt.figure()
plt.title('Decision Tree - Sensitivity')
plt.plot(Ks, sensitivities_DT_0,'m', label='baseline')
plt.plot(Ks, sensitivities_DT_4,'coral', label='pre-proc4')
plt.plot(Ks, sensitivities_DT_poda0,'y', label='baseline w/ prune')
plt.plot(Ks, sensitivities_DT_poda4,'greenyellow', label='pre-proc4 w/ prune')
plt.xlabel("min sam")
plt.ylabel("sensitivity")
plt.xticks(np.arange(1, 21, step=2))
plt.yticks(np.arange(0, 1, step=0.1))
plt.legend()
plt.show()


# # Random Forest

# In[121]:


ntrees = range(1,21,2)

accurracies_RF_ntree_0 = []
costs_RF_ntree_0 = []
sensitivities_RF_ntree_0 = []

accurracies_RF_ntree_0poda = []
costs_RF_ntree_0poda = []
sensitivities_RF_ntree_0poda = []

accurracies_RF_ntree_4 = []
costs_RF_ntree_4 = []
sensitivities_RF_ntree_4 = []

accurracies_RF_ntree_4poda = []
costs_RF_ntree_4poda = []
sensitivities_RF_ntree_4poda = []

X_train0, X_test0, Y_train0, Y_test0 = model_selection.train_test_split(X,Y, train_size = 0.7, test_size = 0.3, stratify = Y)
X_train4, X_test4, Y_train4, Y_test4 = model_selection.train_test_split(X_preproc_4,Y, train_size = 0.7, test_size = 0.3, stratify = Y)
for n in ntrees:
    
    print("n trees =", n)
    
    print("--- 0 s/poda ---")
    
    rf = RandomForestClassifier(n_estimators =n)
    model = rf.fit(X_train0, Y_train0)
    predYClf = model.predict(X_test0)
     
    cmClf = metrics.confusion_matrix(Y_test0, predYClf,labels=[1, 0])
    accuracy = model.score(X_test0, Y_test0)
    Sens, Spec, cost = compute_metrics(cmClf)

    accurracies_RF_ntree_0.append(accuracy)
    costs_RF_ntree_0.append(cost[0])
    sensitivities_RF_ntree_0.append(Sens[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    
    print("---0 c/poda ---")
    
    rf = RandomForestClassifier(n_estimators =n, max_depth=10)
    model = rf.fit(X_train0, Y_train0)
    predYClf = model.predict(X_test0)
     
    cmClf = metrics.confusion_matrix(Y_test0, predYClf,labels=[1, 0])
    accuracy = model.score(X_test0, Y_test0)
    Sens, Spec, cost = compute_metrics(cmClf)

    accurracies_RF_ntree_0poda.append(accuracy)
    costs_RF_ntree_0poda.append(cost[0])
    sensitivities_RF_ntree_0poda.append(Sens[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    
    print("---4 s/poda---")
    
    rf = RandomForestClassifier(n_estimators =n, max_depth=10)
    model = rf.fit(X_train4, Y_train4)
    predYClf = model.predict(X_test4)
    cmClf = metrics.confusion_matrix(Y_test4, predYClf,labels=[1, 0])
    accuracy = model.score(X_test4, Y_test4)
    Sens, Spec, cost = compute_metrics(cmClf)
    accurracies_RF_ntree_4.append(accuracy)
    costs_RF_ntree_4.append(cost[0])
    sensitivities_RF_ntree_4.append(Sens[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    
    print("---4 c/poda---")
    
    rf = RandomForestClassifier(n_estimators =n, max_depth=10)
    model = rf.fit(X_train4, Y_train4)
    predYClf = model.predict(X_test4)
    cmClf = metrics.confusion_matrix(Y_test4, predYClf,labels=[1, 0])
    accuracy = model.score(X_test4, Y_test4)
    Sens, Spec, cost = compute_metrics(cmClf)
    accurracies_RF_ntree_4poda.append(accuracy)
    costs_RF_ntree_4poda.append(cost[0])
    sensitivities_RF_ntree_4poda.append(Sens[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    
    print("--------------------------------------------------------------")

print("Accuracies 0: ", accurracies_RF_ntree_0)
print("Costs 0: ", costs_RF_ntree_0)
print("Sensitivities 0: ", sensitivities_RF_ntree_0)

print("Accuracies 0 poda: ", accurracies_RF_ntree_0poda)
print("Costs 0 poda: ", costs_RF_ntree_0poda)
print("Sensitivities 0 poda: ", sensitivities_RF_ntree_0poda)

print("Accuracies 4: ", accurracies_RF_ntree_4)
print("Costs 4: ", costs_RF_ntree_4)
print("Sensitivities 4: ", sensitivities_RF_ntree_4)

print("Accuracies 4 poda: ", accurracies_RF_ntree_4poda)
print("Costs 4 poda: ", costs_RF_ntree_4poda)
print("Sensitivities 4 poda: ", sensitivities_RF_ntree_4poda)


# In[123]:


Ks = range(1,21,2)

plt.figure()
plt.title('Random Forest - Accuracy')
plt.plot(Ks, accurracies_RF_ntree_0,'m', label='baseline')
plt.plot(Ks, accurracies_RF_ntree_4,'coral', label='pre-proc4')
plt.plot(Ks, accurracies_RF_ntree_0poda,'y', label='baseline w/ prune')
plt.plot(Ks, accurracies_RF_ntree_4poda,'greenyellow', label='pre-proc4 w/ prune')
plt.legend()
plt.xlabel("num trees")
plt.ylabel("accuracy")
plt.xticks(np.arange(1, 21, step=2))
plt.yticks(np.arange(0.5, 1, step=0.1))
plt.show()

plt.figure()
plt.title('Random Forest - Sensitivity')
plt.plot(Ks, sensitivities_RF_ntree_0,'m', label='baseline')
plt.plot(Ks, sensitivities_RF_ntree_4,'coral', label='pre-proc4')
plt.plot(Ks, sensitivities_RF_ntree_0poda,'y', label='baseline w/ prune')
plt.plot(Ks, sensitivities_RF_ntree_4poda,'greenyellow', label='pre-proc4 w/ prune')
plt.xlabel("num trees")
plt.ylabel("sensitivity")
plt.xticks(np.arange(1, 21, step=2))
plt.yticks(np.arange(0, 1, step=0.1))
plt.legend()
plt.show()


# # K-fold Baseline

# In[133]:


kf = model_selection.KFold(n_splits = 10)
kfold_baseline_accurracy_knn, kfold_baseline_costs_knn = [], []
kfold_baseline_accurracy_dtp, kfold_baseline_costs_dtp, kfold_baseline_accurracy_dt, kfold_baseline_costs_dt = [], [], [], []
kfold_baseline_accurracy_rfp, kfold_baseline_costs_rfp, kfold_baseline_accurracy_rf, kfold_baseline_costs_rf = [], [], [], []

ks = range(1,31)

for train_index, test_index in kf.split(X,Y):
    #------------------------------KNN--------------------------
    X_train, X_test = X.iloc[train_index], X.iloc[test_index]
    Y_train, Y_test = Y[train_index], Y[test_index]
    
    knn = neighbors.KNeighborsClassifier(n_neighbors = 5)
    model = knn.fit(X_train,Y_train)
    predY = model.predict(X_test)

    cm = metrics.confusion_matrix(Y_test, predY,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test, Y_test)
    kfold_baseline_accurracy_knn.append(accurracy)
    kfold_baseline_costs_knn.append(cost[0])
    #------------------------------DT--------------------------
    
    #print("---0 s/poda---")
    clf = tree.DecisionTreeClassifier(min_samples_split=5)
    model = clf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_dt.append(accuracy)
    kfold_baseline_costs_dt.append(cost[0])
    
    #print("---0 c/poda---")
    clf = tree.DecisionTreeClassifier(max_depth=8,min_samples_split=5)
    model = clf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_dtp.append(accuracy)
    kfold_baseline_costs_dtp.append(cost[0])
    
    #------------------------------RF--------------------------
    
    #print("--- 0 s/poda ---")
    
    rf = RandomForestClassifier(n_estimators =n)
    model = rf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
     
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)

    kfold_baseline_accurracy_rf.append(accuracy)
    kfold_baseline_costs_rf.append(cost[0])
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    
    #print("---0 c/poda ---")
    
    rf = RandomForestClassifier(n_estimators =n, max_depth=10)
    model = rf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
     
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)

    kfold_baseline_accurracy_rfp.append(accuracy)
    kfold_baseline_costs_rfp.append(cost[0])


# In[134]:


plt.figure()
aa = np.full((len(kfold_baseline_accurracy_knn)), mean_accuracy_NB_baseline)
plt.title('Accuracy for min sample split')
plt.plot(Ks, kfold_baseline_accurracy_knn,'m', label='knn')
plt.plot(Ks, kfold_baseline_accurracy_dt,'coral', label='DT')
plt.plot(Ks, kfold_baseline_accurracy_dtp,'y', label='DT w/ prune')
plt.plot(Ks, kfold_baseline_accurracy_rf,'greenyellow', label='RF')
plt.plot(Ks, kfold_baseline_accurracy_rfp,'b', label='RF w/ prune')
plt.plot(Ks, aa,'r:', label='NB baseline')
plt.legend()
plt.xlabel("K")
plt.ylabel("Accuracy")
plt.title("K-fold Baseline")
plt.xticks(np.arange(1, 21, step=2))
plt.yticks(np.arange(0.5, 1, step=0.1))
plt.show()


# In[136]:


kf = model_selection.KFold(n_splits = 10)
kfold_baseline_accurracy_knn, kfold_baseline_costs_knn = [], []
kfold_baseline_accurracy_dtp, kfold_baseline_costs_dtp, kfold_baseline_accurracy_dt, kfold_baseline_costs_dt = [], [], [], []
kfold_baseline_accurracy_rfp, kfold_baseline_costs_rfp, kfold_baseline_accurracy_rf, kfold_baseline_costs_rf = [], [], [], []

ks = range(1,11)

for train_index, test_index in kf.split(X_preproc_1,Y):
    #------------------------------KNN--------------------------
    X_train, X_test = X_preproc_1.iloc[train_index], X_preproc_1.iloc[test_index]
    Y_train, Y_test = Y[train_index], Y[test_index]
    
    knn = neighbors.KNeighborsClassifier(n_neighbors = 5)
    model = knn.fit(X_train,Y_train)
    predY = model.predict(X_test)

    cm = metrics.confusion_matrix(Y_test, predY,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test, Y_test)
    kfold_baseline_accurracy_knn.append(accurracy)
    #------------------------------DT--------------------------
    
    #print("---0 s/poda---")
    clf = tree.DecisionTreeClassifier(min_samples_split=5)
    model = clf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_dt.append(accuracy)
    
    #print("---0 c/poda---")
    clf = tree.DecisionTreeClassifier(max_depth=8,min_samples_split=5)
    model = clf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_dtp.append(accuracy)
    
    #------------------------------RF--------------------------
    
    #print("--- 0 s/poda ---")
    
    rf = RandomForestClassifier(n_estimators =n)
    model = rf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
     
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_rf.append(accuracy)
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    
    #print("---0 c/poda ---")
    
    rf = RandomForestClassifier(n_estimators =n, max_depth=10)
    model = rf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
     
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)

    kfold_baseline_accurracy_rfp.append(accuracy)


# In[137]:


plt.figure()
aa = np.full((len(kfold_baseline_accurracy_knn)), mean_accuracy_NB_pre1)
plt.title('Accuracy for min sample split')
plt.plot(ks, kfold_baseline_accurracy_knn,'m', label='knn')
plt.plot(ks, kfold_baseline_accurracy_dt,'coral', label='DT')
plt.plot(ks, kfold_baseline_accurracy_dtp,'y', label='DT w/ prune')
plt.plot(ks, kfold_baseline_accurracy_rf,'greenyellow', label='RF')
plt.plot(ks, kfold_baseline_accurracy_rfp,'b', label='RF w/ prune')
plt.plot(ks, aa,'r:', label='NB pre-proc1')
plt.legend()
plt.xlabel("K")
plt.ylabel("Accuracy")
plt.title("K-fold Pre-processing 1")
plt.xticks(np.arange(1, 21, step=2))
plt.yticks(np.arange(0.5, 1, step=0.1))
plt.show()


# In[31]:


kf = model_selection.KFold(n_splits = 10)
kfold_baseline_accurracy_knn, kfold_baseline_costs_knn = [], []
kfold_baseline_accurracy_dtp, kfold_baseline_costs_dtp, kfold_baseline_accurracy_dt, kfold_baseline_costs_dt = [], [], [], []
kfold_baseline_accurracy_rfp, kfold_baseline_costs_rfp, kfold_baseline_accurracy_rf, kfold_baseline_costs_rf = [], [], [], []

ks = range(1,11)

for train_index, test_index in kf.split(X_preproc_3,Y_preproc_3):
    #------------------------------KNN--------------------------
    X_train, X_test = X_preproc_3.iloc[train_index], X_preproc_3.iloc[test_index]
    Y_train, Y_test = Y_preproc_3[train_index], Y_preproc_3[test_index]
    
    knn = neighbors.KNeighborsClassifier(n_neighbors = 5)
    model = knn.fit(X_train,Y_train)
    predY = model.predict(X_test)

    cm = metrics.confusion_matrix(Y_test, predY,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test, Y_test)
    kfold_baseline_accurracy_knn.append(accurracy)
    #------------------------------DT--------------------------
    
    #print("---0 s/poda---")
    clf = tree.DecisionTreeClassifier(min_samples_split=5)
    model = clf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_dt.append(accuracy)
    
    #print("---0 c/poda---")
    clf = tree.DecisionTreeClassifier(max_depth=8,min_samples_split=5)
    model = clf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_dtp.append(accuracy)
    
    #------------------------------RF--------------------------
    
    #print("--- 0 s/poda ---")
    
    rf = RandomForestClassifier(n_estimators =n)
    model = rf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
     
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_rf.append(accuracy)
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    
    #print("---0 c/poda ---")
    
    rf = RandomForestClassifier(n_estimators =n, max_depth=10)
    model = rf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
     
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)

    kfold_baseline_accurracy_rfp.append(accuracy)


# In[32]:


plt.figure()
aa = np.full((len(kfold_baseline_accurracy_knn)), mean_accuracy_NB_pre3)
plt.title('Accuracy for min sample split')
plt.plot(ks, kfold_baseline_accurracy_knn,'m', label='knn')
plt.plot(ks, kfold_baseline_accurracy_dt,'coral', label='DT')
plt.plot(ks, kfold_baseline_accurracy_dtp,'y', label='DT w/ prune')
plt.plot(ks, kfold_baseline_accurracy_rf,'greenyellow', label='RF')
plt.plot(ks, kfold_baseline_accurracy_rfp,'b', label='RF w/ prune')
plt.plot(ks, aa,'r:', label='NB pre-proc3')
plt.legend()
plt.xlabel("K")
plt.ylabel("Accuracy")
plt.title("K-fold Pre-processing 3")
plt.xticks(np.arange(1, 10, step=2))
plt.show()


# In[33]:


kf = model_selection.KFold(n_splits = 10)
kfold_baseline_accurracy_knn, kfold_baseline_costs_knn = [], []
kfold_baseline_accurracy_dtp, kfold_baseline_costs_dtp, kfold_baseline_accurracy_dt, kfold_baseline_costs_dt = [], [], [], []
kfold_baseline_accurracy_rfp, kfold_baseline_costs_rfp, kfold_baseline_accurracy_rf, kfold_baseline_costs_rf = [], [], [], []

ks = range(1,11)

for train_index, test_index in kf.split(X_preproc_4,Y):
    #------------------------------KNN--------------------------
    X_train, X_test = X_preproc_4.iloc[train_index], X_preproc_4.iloc[test_index]
    Y_train, Y_test = Y[train_index], Y[test_index]
    
    knn = neighbors.KNeighborsClassifier(n_neighbors = 5)
    model = knn.fit(X_train,Y_train)
    predY = model.predict(X_test)

    cm = metrics.confusion_matrix(Y_test, predY,labels=[1, 0])
    Sens, Spec, cost = compute_metrics(cm)
    accurracy = model.score(X_test, Y_test)
    kfold_baseline_accurracy_knn.append(accurracy)
    #------------------------------DT--------------------------
    
    #print("---0 s/poda---")
    clf = tree.DecisionTreeClassifier(min_samples_split=5)
    model = clf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_dt.append(accuracy)
    
    #print("---0 c/poda---")
    clf = tree.DecisionTreeClassifier(max_depth=8,min_samples_split=5)
    model = clf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_dtp.append(accuracy)
    
    #------------------------------RF--------------------------
    
    #print("--- 0 s/poda ---")
    
    rf = RandomForestClassifier(n_estimators =n)
    model = rf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
     
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)
    kfold_baseline_accurracy_rf.append(accuracy)
    
    print(cmClf)
    print("Accurracy:", accuracy)
    print("Sensitivity:", Sens[0])
    print("Specificity:", Spec[0])
    print("Cost:", cost)
    
    #print("---0 c/poda ---")
    
    rf = RandomForestClassifier(n_estimators =n, max_depth=10)
    model = rf.fit(X_train, Y_train)
    predYClf = model.predict(X_test)
     
    cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
    accuracy = model.score(X_test, Y_test)
    Sens, Spec, cost = compute_metrics(cmClf)

    kfold_baseline_accurracy_rfp.append(accuracy)


# In[34]:


plt.figure()
aa = np.full((len(kfold_baseline_accurracy_knn)), mean_accuracy_NB_pre4)
plt.title('Accuracy for min sample split')
plt.plot(ks, kfold_baseline_accurracy_knn,'m', label='knn')
plt.plot(ks, kfold_baseline_accurracy_dt,'coral', label='DT')
plt.plot(ks, kfold_baseline_accurracy_dtp,'y', label='DT w/ prune')
plt.plot(ks, kfold_baseline_accurracy_rf,'greenyellow', label='RF')
plt.plot(ks, kfold_baseline_accurracy_rfp,'b', label='RF w/ prune')
plt.plot(ks, aa,'r:', label='NB pre-proc4')
plt.legend()
plt.xlabel("K")
plt.ylabel("Accuracy")
plt.title("K-fold Pre-processing 4")
plt.xticks(np.arange(1, 10, step=2))
plt.show()


# In[83]:


def plot_confusion_matrix(cm, classes,
                          normalize=False,
                          title='Confusion matrix',
                          cmap=plt.cm.Blues):
    """
    This function prints and plots the confusion matrix.
    Normalization can be applied by setting `normalize=True`.
    """
    if normalize:
        cm = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis]
        print("Normalized confusion matrix")
    else:
        print('Confusion matrix, without normalization')

    print(cm)

    plt.imshow(cm, interpolation='nearest', cmap=cmap)
    plt.title(title)
    plt.colorbar()
    tick_marks = np.arange(len(classes))
    plt.xticks(tick_marks, classes, rotation=45)
    plt.yticks(tick_marks, classes)

    fmt = '.2f' if normalize else 'd'
    thresh = cm.max() / 2.
    for i, j in itertools.product(range(cm.shape[0]), range(cm.shape[1])):
        plt.text(j, i, format(cm[i, j], fmt),
                 horizontalalignment="center",
                 color="white" if cm[i, j] > thresh else "black")

    plt.ylabel('True label')
    plt.xlabel('Predicted label')
    plt.tight_layout()

X_train, X_test, Y_train, Y_test = model_selection.train_test_split(X,Y, train_size = 0.7, test_size = 0.3, stratify = Y)

rf = RandomForestClassifier(n_estimators=20, max_depth=10)
model = rf.fit(X_train, Y_train)
predYClf = model.predict(X_test)

cmClf = metrics.confusion_matrix(Y_test, predYClf,labels=[1, 0])
accuracy = model.score(X_test, Y_test)
Sens, Spec, cost = compute_metrics(cmClf)

print(cmClf)
print("Acurracy: ", accurracy)
plt.figure()
plot_confusion_matrix(cmClf , classes=labels, title='Confusion matrix')
plt.show()


# In[ ]:





# In[ ]:




