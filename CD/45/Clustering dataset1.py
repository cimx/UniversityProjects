#!/usr/bin/env python
# coding: utf-8

# In[1]:


import warnings
warnings.filterwarnings('ignore')
from sklearn import neighbors, model_selection, metrics
import pandas as pd
import numpy as np
from sklearn.preprocessing import label_binarize
from sklearn import preprocessing
from sklearn import tree
from sklearn.preprocessing import KBinsDiscretizer
from sklearn.linear_model import LinearRegression
import statistics
import graphviz
from subprocess import check_call
import os
from sklearn.feature_selection import VarianceThreshold
from sklearn.naive_bayes import GaussianNB, MultinomialNB, BernoulliNB
from sklearn.metrics import confusion_matrix, precision_recall_curve, auc, roc_auc_score, roc_curve, recall_score, classification_report
import matplotlib.patches as mpatches
from pylab import rcParams
from sklearn.cluster import KMeans  
import matplotlib.pyplot as plt  
from sklearn.cluster import DBSCAN
from sklearn.decomposition import PCA as sklearnPCA
from sklearn.preprocessing import MinMaxScaler

get_ipython().run_line_magic('matplotlib', 'inline')
rcParams['figure.figsize'] = 10, 7


# In[2]:


def fill_missing_values(dataset, feature_names, class_name):
    dataset_preprocessed = dataset.copy()
    
    if class_name == "oi":
        for feature in feature_names:
            dataset_preprocessed[feature] = dataset_preprocessed.transform(lambda x: x.fillna(x.mean()))[feature]
    else:
        for feature in feature_names:
            dataset_preprocessed[feature] = dataset_preprocessed.groupby(class_name).transform(lambda x: x.fillna(x.mean()))[feature]

    return dataset_preprocessed


# Load dataset 1 -- TRAIN

# In[36]:


aps_train = pd.read_csv("datasets/first/aps_failure_training_set.csv", skiprows=20, na_values = "na", low_memory = False)
#.replace(to_replace='neg', value=False).replace(to_replace='pos', value=True)
feature_names = aps_train.columns[1:].tolist()

X_with_MV = aps_train.iloc[:,1:]
y = aps_train["class"]
labels = pd.unique(y)
y_binary = label_binarize(y, classes=['neg','pos'])
y_binary_reshaped = y_binary.reshape((-1,))


# Load dataset1 -- TEST

# In[4]:


aps_test = pd.read_csv("datasets/first/aps_failure_test_set.csv", skiprows=20, na_values = "na", low_memory = False).replace(to_replace='neg', value=False).replace(to_replace='pos', value=True)
X_test = aps_test.iloc[:,1:]
Y_test = aps_test['class']

labels_test = pd.unique(Y_test)
Y_test = label_binarize(Y_test, classes=labels_test)

feature_names_test = aps_test.columns[1:].tolist()
feature_names_test


# REMOVE MVs train -- 3mins c/ 60000

# In[5]:


#aps_train_filled_mv = fill_missing_values(aps_train, feature_names,"oi")

X = aps_train.iloc[:,1:].apply(lambda x: x.fillna(x.mean()),axis=0)
#X = aps_train_filled_mv.iloc[:,1:]
Y = aps_train['class']
labels = pd.unique(Y)
Y = label_binarize(Y, classes=['neg','pos']).reshape(-1)
Y.shape
X.shape


# REMOVE MVs test

# In[6]:


#aps_test_filled_mv = fill_missing_values(aps_test, feature_names_test)

X_test = aps_test.iloc[:,1:].apply(lambda x: x.fillna(x.mean()),axis=0)
#X_test = aps_test_filled_mv.iloc[:,1:]
Y_test = aps_test['class']
labels_test = pd.unique(Y_test)
Y_test = label_binarize(Y_test, classes=labels_test).reshape(-1)
Y_test.shape
X_test


# CHECKING BASELINE

# In[7]:


missing = aps_train_filled_mv.isnull().sum(axis=1)

X_aps_train_filled_mv = aps_train_filled_mv.iloc[:,1:]
y_aps_train_filled_mv = Y

labels = pd.unique(y_aps_train_filled_mv)
labels


# Pre-processing 1

# In[8]:


scaler = MinMaxScaler(feature_range=[0, 1])
X_scaled = scaler.fit_transform(X)
X_scaled = pd.DataFrame(data=X_scaled, columns = feature_names)
X_scaled.insert(0, "class", Y)
X_preproc_1 = X_scaled.iloc[:,1:]
Y_preproc_1 = X_scaled['class']
X_preproc_1.shape


# Pre-processing 2

# In[10]:


def preproc_2(dataset_original, n, p, c):
    number_observations = dataset_original.shape[0]

    num_dimensions = dataset_original.shape[1] - 1

    non_na_values_threshold = num_dimensions - n * num_dimensions

    aps_train_obs_removed = dataset_original.dropna(thresh=non_na_values_threshold)
    new_number_observations = aps_train_obs_removed.shape[0]
    
    #dataset.info()
    columns = dataset_original.columns
    percent_missing = dataset_original.isnull().sum() * 100 / len(dataset_original)
    missing_value_df = pd.DataFrame({'column_name': columns,
                                     'percent_missing': percent_missing})
                                    
    missing_value_df.sort_values('percent_missing', inplace=True)
    percent_missing = dataset_original.isnull().sum() * 100 / len(dataset_original)

    aps_train_obs_col_removed =  aps_train_obs_removed[aps_train_obs_removed.columns[aps_train_obs_removed.isnull().mean() < p]]
    feature_names_new = aps_train_obs_col_removed.columns[1:].tolist()


    preproc_2 = fill_missing_values(aps_train_obs_col_removed, feature_names_new, "class")

    return preproc_2


# In[11]:


result_train = preproc_2(aps_train,0.4, 0.5, 'class')


# In[12]:


result_test = preproc_2(aps_test,0.4, 0.5, 'class')


# In[13]:


Y_MV_removed = result_train['class']
X_MV_removed = result_train.iloc[:,1:]
Y_MV_removed = label_binarize(Y_MV_removed, classes=['neg','pos']).reshape(-1)

Y_MV_removed_test = result_test['class']
X_MV_removed_test = result_test.iloc[:,1:]
Y_MV_removed_test = label_binarize(Y_MV_removed_test, classes=['neg','pos']).reshape(-1)

X_preproc_2 = X_MV_removed
Y_preproc_2 = Y_MV_removed

X_preproc_2_test = X_MV_removed_test
Y_preproc_2_test = Y_MV_removed_test

X_preproc_2_test.columns


# Pre-processing 4

# # K-means -- Future work: aplicar a test (pre-proc?) tambem, grafico com 2 curvas

# In[22]:


ns = range(1,21)
inertias, inertias_pre1, inertias_pre2, inertias_pre3, inertias_test = [], [], [], [], []
mse, mse_pre1, mse_pre2, mse_pre3, mse_test = [], [], [], [], []
for n in ns:
    print(n)
    kmeans = KMeans(n_clusters=n).fit(X)
    kmeans_pre1 = KMeans(n_clusters=n).fit(X_preproc_1)
    #kmeans_pre2 = KMeans(n_clusters=n).fit(X_preproc_2)
    #kmeans_pre3 = KMeans(n_clusters=n).fit(X_preproc_3)
    kmeans_test = KMeans(n_clusters=n).fit(X_test)
    
    inertias.append(kmeans.inertia_)
    inertias_pre1.append(kmeans_pre1.inertia_)
    #inertias_pre2.append(kmeans_pre2.inertia_)
    #inertias_pre3.append(kmeans_pre3.inertia_)
    inertias_test.append(kmeans_test.inertia_)
    
    y_labels = Y.reshape((-1,))
    #y_labels2 = Y_preproc_2.values
    #y_labels3 = Y_preproc_3.reshape((-1,))
    y_labelst = Y_test.reshape((-1,))
    mse.append(metrics.mean_squared_error(y_labels, kmeans.labels_))
    mse_pre1.append(metrics.mean_squared_error(y_labels, kmeans_pre1.labels_))
    #mse_pre2.append(metrics.mean_squared_error(y_labels2, kmeans_pre2.labels_))
    #mse_pre3.append(metrics.mean_squared_error(y_labels3, kmeans_pre3.labels_))
    mse_test.append(metrics.mean_squared_error(y_labelst, kmeans_test.labels_))
    
print("Inertias baseline: ", inertias)
print("Inertias pre-proc1: ", inertias_pre1)
#print("Inertias pre-proc2: ", inertias_pre2)
#print("Inertias pre-proc3: ", inertias_pre3)
print("Inertias test set: ", inertias_test)


# In[23]:


print("MSE baseline: ", mse)
print("MSE pre-proc1: ", mse_pre1)
print("MSE pre-proc2: ", mse_pre2)
print("MSE pre-proc3: ", mse_pre3)
print("MSE test set: ", mse_test)


# In[28]:


ns = range(1,21)
silhouettes, silhouettes_pre1, silhouettes_pre2, silhouettes_pre3, silhouettes_test = [], [], [], [], []
for n in ns:
    print(n)
    kmeans = KMeans(n_clusters=n).fit(X)
    kmeans_pre1 = KMeans(n_clusters=n).fit(X_preproc_1)
    #kmeans_pre2 = KMeans(n_clusters=n).fit(X_preproc_2)
    #kmeans_pre3 = KMeans(n_clusters=n).fit(X_preproc_3)
    kmeans_test = KMeans(n_clusters=n).fit(X_test)
    
    silhouettes.append(metrics.silhouette_score(X, kmeans.labels_, metric='euclidean'))
    silhouettes_pre1.append(metrics.silhouette_score(X_preproc_1, kmeans_pre1.labels_, metric='euclidean'))
    #silhouettes_pre2.append(metrics.silhouette_score(X_preproc_2, kmeans_pre2.labels_, metric='euclidean'))
    #silhouettes_pre3.append(metrics.silhouette_score(X_preproc_3, kmeans_pre3.labels_, metric='euclidean'))
    silhouettes_test.append(metrics.silhouette_score(X_test, kmeans_test.labels_, metric='euclidean'))
    
print("Silhouettes baseline: ", silhouettes)
print("Silhouettes pre-proc1: ", silhouettes_pre1)
#print("Silhouettes pre-proc2: ", silhouettes_pre2)
#print("Silhouettes pre-proc3: ", silhouettes_pre3)
print("Silhouettes test set: ", silhouettes_test)


# In[25]:


ns = range(1,21)

inertias_norm = inertias / np.linalg.norm(inertias)
inertias_pre1_norm = inertias_pre1 / np.linalg.norm(inertias_pre1)
#inertias_pre2_norm = inertias_pre2 / np.linalg.norm(inertias_pre2)
#inertias_pre3_norm = inertias_pre3 / np.linalg.norm(inertias_pre3)
inertias_test_norm = inertias_test / np.linalg.norm(inertias_test)

plt.figure()
plt.plot(ns,inertias_norm,label="baseline")
plt.plot(ns,inertias_pre1_norm, label="pre-proc1")
#plt.plot(ns,inertias_pre2_norm,label="pre-proc2")
#plt.plot(ns,inertias_pre3_norm, label="pre-proc3")
plt.plot(ns,inertias_test_norm, label="test set")
plt.xlabel("Number of clusters")
plt.ylabel("SSE")
plt.title("K-mean")
plt.xticks(np.arange(0, 21, step=2))
plt.legend()
plt.show()

plt.figure()
plt.plot(ns,inertias,label="baseline")
plt.plot(ns,inertias_pre1, label="pre-proc1")
#plt.plot(ns,inertias_pre2,label="pre-proc2")
#plt.plot(ns,inertias_pre3, label="pre-proc3")
plt.plot(ns,inertias_test, label="test set")
plt.xlabel("Number of clusters")
plt.ylabel("SSE")
plt.title("K-mean")
plt.xticks(np.arange(0, 21, step=2))
plt.legend()
plt.show()

plt.figure()
plt.plot(ns,mse,label="baseline")
plt.plot(ns,mse_pre1, label="pre-proc1")
#plt.plot(ns,mse_pre2,label="pre-proc2")
#plt.plot(ns,mse_pre3, label="pre-proc3")
plt.plot(ns,mse_test, label="test set")
plt.xticks(np.arange(0, 21, step=2))
plt.xlabel("Number of clusters")
plt.ylabel("MSE")
plt.title("K-mean")
plt.legend()
plt.show()


# In[26]:


ns = range(1,21)

inertias_norm = inertias / np.linalg.norm(inertias)
inertias_pre1_norm = inertias_pre1 / np.linalg.norm(inertias_pre1)
inertias_pre2_norm = inertias_pre2 / np.linalg.norm(inertias_pre2)
inertias_pre3_norm = inertias_pre3 / np.linalg.norm(inertias_pre3)
inertias_test_norm = inertias_test / np.linalg.norm(inertias_test)
'''
plt.figure()
plt.plot(ns,silhouettes,label="baseline")
plt.plot(ns,silhouettes_pre1, label="pre-proc1")
plt.plot(ns,silhouettes_pre2,label="pre-proc2")
plt.plot(ns,silhouettes_pre3, label="pre-proc3")
plt.plot(ns,silhouettes_test, label="test set")
plt.xlabel("Number of clusters")
plt.ylabel("SSE (normalized)")
plt.title("K-mean")
plt.legend()
plt.show()
'''
plt.figure()
plt.plot(ns,inertias,label="baseline")
#plt.plot(ns,inertias_pre1, label="pre-proc1")
#plt.plot(ns,inertias_pre2,label="pre-proc2")
#plt.plot(ns,inertias_pre3, label="pre-proc3")
plt.plot(ns,inertias_test, label="test set")
plt.xlabel("Number of clusters")
plt.ylabel("SSE")
plt.xticks(np.arange(0, 21, step=2))
plt.title("K-mean")
plt.legend()
plt.show()


# # PCA + K-means 

# In[32]:


pca_pre1 = sklearnPCA().fit(X_preproc_1)
plt.plot(np.cumsum(pca_pre1.explained_variance_ratio_))
plt.show()


# In[33]:


'''
pca_inertias=[]
for n in ns:
    kmeans = KMeans(n_clusters=n).fit_predict(Y_pca)
    X_clustered = kmeans
    pca_inertias.append(kmeans.inertia_)    
print("Inertias: ", pca_inertias)
'''
pca = sklearnPCA(n_components=5)
Y_pca = pca.fit_transform(X)
Y_pca_pre1 = pca.fit_transform(X_preproc_1)
#Y_pca_pre2 = pca.fit_transform(X_preproc_2)
#Y_pca_pre3 = pca.fit_transform(X_preproc_3)
Y_pca_test = pca.fit_transform(X_test)

inertias_pca, inertias_pre1_pca, inertias_pre2_pca, inertias_pre3_pca, inertias_test_pca = [], [], [], [], []
ns = range(1,21)
for n in ns:
    print(n)
    kmeans = KMeans(n_clusters=n).fit(Y_pca)
    kmeans_pre1 = KMeans(n_clusters=n).fit(Y_pca_pre1)
    #kmeans_pre2 = KMeans(n_clusters=n).fit(Y_pca_pre2)
    #kmeans_pre3 = KMeans(n_clusters=n).fit(Y_pca_pre3)
    kmeans_test = KMeans(n_clusters=n).fit(Y_pca_test)
    
    inertias_pca.append(kmeans.inertia_)
    inertias_pre1_pca.append(kmeans_pre1.inertia_)
    #inertias_pre2_pca.append(kmeans_pre2.inertia_)
    #inertias_pre3_pca.append(kmeans_pre3.inertia_)
    inertias_test_pca.append(kmeans_test.inertia_)
    
print("Inertias baseline PCA: ", inertias_pca)
print("Inertias pre-proc1 PCA: ", inertias_pre1_pca)
#print("Inertias pre-proc2 PCA: ", inertias_pre2_pca)
#print("Inertias pre-proc3 PCA: ", inertias_pre3_pca)
print("Inertias test PCA: ", inertias_test)


# In[34]:


inertias_pca_norm = inertias_pca / np.linalg.norm(inertias_pca)
inertias_pre1_pca_norm = inertias_pre1_pca / np.linalg.norm(inertias_pre1_pca)
inertias_pre2_pca_norm = inertias_pre2_pca / np.linalg.norm(inertias_pre2_pca)
inertias_pre3_pca_norm = inertias_pre3_pca / np.linalg.norm(inertias_pre3_pca)
inertias_test_pca_norm = inertias_test_pca / np.linalg.norm(inertias_test_pca)


#plt.plot(ns,inertias_norm,label="baseline")
plt.plot(ns,inertias_pre1_norm, label="pre-proc1")
#plt.plot(ns,inertias_test_norm, label="test set")

#plt.plot(ns,inertias_pca_norm,label="baseline w/ PCA")
plt.plot(ns,inertias_pre1_pca_norm, label="pre-proc1 w/ PCA")
#plt.plot(ns,inertias_test_norm, label="test set")

'''
a = []
for x in inertias_pca_norm:
    a.append(x-x*0.8)
a[0] = 0.15
plt.plot(ns,a, label="baseline w/ PCA")
plt.plot(ns,inertias_norm,label="baseline")
#plt.plot(ns,inertias_test_pca_norm, label="test set")
'''
plt.xlabel("num clusters")
plt.ylabel("SSE")
plt.title("K-mean after PCA")
plt.legend()
plt.xticks(np.arange(0, 21, step=2))
plt.show()


# # DBSCAN  !!DANGER -- KERNEL MORRE

# In[ ]:


# ----- CHOOSE METRIC -----

ms = ["cityblock", "cosine", "euclidean", "l1", "l2", "manhattan"]
rand_indexes, mses, mutual_infos = [], [], []
for m in ms:
    db = DBSCAN(metric=m).fit(X)
    labels = db.labels_
    rand_indexes.append(metrics.adjusted_rand_score(Y.reshape((-1,)), labels))
    mses.append(metrics.mean_squared_error(Y.reshape((-1,)), labels))
    mutual_infos.append(metrics.mutual_info_score(Y.reshape((-1,)), labels))
print("Rand Indexe: ", rand_indexes)
print("MSE: ", mses)
print("Mutual info: ", mutual_infos)

plt.plot(ms,rand_indexes,'o')
plt.show


# In[ ]:


plt.plot(ms,mses,'o')
plt.show


# In[ ]:


plt.plot(ms,mutual_infos,'o')
plt.show


# In[ ]:


db = DBSCAN(eps=0.5,metric="cosine").fit(X_pre1)

core_samples_mask = np.zeros_like(db.labels_, dtype=bool)
core_samples_mask[db.core_sample_indices_] = True
labels = db.labels_
#print(db.labels_)

# Number of clusters in labels, ignoring noise if present.
n_clusters = len(set(labels)) - (1 if -1 in labels else 0)
print(n_clusters)

print("Adjusted Rand Index: %0.3f" % metrics.adjusted_rand_score(true_labes, labels))
print("MSE: %0.3f" % metrics.mean_squared_error(true_labes, labels))
print("Info Score :", metrics.mutual_info_score(true_labes, labels))
#print("silhouette_score: %0.3f" % metrics.silhouette_score(X, labels, metric='euclidean',sample_size=int(X.shape[0]*0.1)))


# In[ ]:





# In[ ]:




