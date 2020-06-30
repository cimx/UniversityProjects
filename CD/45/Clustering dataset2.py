#!/usr/bin/env python
# coding: utf-8

# In[109]:


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

import scipy.cluster.hierarchy as sch
from sklearn.cluster import AgglomerativeClustering

get_ipython().run_line_magic('matplotlib', 'inline')
rcParams['figure.figsize'] = 10, 7


# In[110]:


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

# In[111]:


green_ds = pd.read_csv("datasets/second/green.csv")
hinselmann_ds = pd.read_csv("datasets/second/hinselmann.csv")
schiller_ds = pd.read_csv("datasets/second/schiller.csv")

green_ds["green_ds"] = 1
green_ds["hinselmann_ds"] = 0
green_ds["schiller_ds"] = 0

hinselmann_ds["green_ds"] = 0
hinselmann_ds["hinselmann_ds"] = 1
hinselmann_ds["schiller_ds"] = 0

schiller_ds["green_ds"] = 0
schiller_ds["hinselmann_ds"] = 0
schiller_ds["schiller_ds"] = 1

aps_train = pd.concat([green_ds,hinselmann_ds,schiller_ds])

class_attributes = ["consensus","experts::0","experts::1","experts::2","experts::3","experts::4","experts::5",]

X = aps_train.drop(labels = class_attributes, axis = "columns")
feature_names = X.columns.tolist()
print(feature_names)
Y = aps_train[class_attributes[0]].values


# Pre-processing 1

# In[112]:


scaler = MinMaxScaler(feature_range=[0, 1])
X_scaled = scaler.fit_transform(X)
X_scaled = pd.DataFrame(data=X_scaled, columns = feature_names)
X_scaled.insert(0, "class", Y)
X_preproc_1 = X_scaled.iloc[:,1:]
Y_preproc_1 = X_scaled['class']
X_preproc_1.shape


# # K-means -- Future work: aplicar a test (pre-proc?) tambem, grafico com 2 curvas

# In[113]:


ns = range(1,21)
inertias, inertias_pre1 = [], []
mse, mse_pre1 = [], []
for n in ns:
    print(n)
    kmeans = KMeans(n_clusters=n).fit(X)
    kmeans_pre1 = KMeans(n_clusters=n).fit(X_preproc_1)
    
    inertias.append(kmeans.inertia_)
    inertias_pre1.append(kmeans_pre1.inertia_)
    
    y_labels = Y.reshape((-1,))
    mse.append(metrics.mean_squared_error(y_labels, kmeans.labels_))
    mse_pre1.append(metrics.mean_squared_error(y_labels, kmeans_pre1.labels_))
    
print("Inertias baseline: ", inertias)
print("Inertias pre-proc1: ", inertias_pre1)


# In[25]:


print("MSE baseline: ", mse)
print("MSE pre-proc1: ", mse_pre1)


# In[49]:


ns = range(2,21)
silhouettes, silhouettes_pre1, silhouettes_pre2, silhouettes_pre3, silhouettes_test = [], [], [], [], []
for n in ns:
    kmeans = KMeans(n_clusters=n).fit(X)
    kmeans_pre1 = KMeans(n_clusters=n).fit(X_preproc_1)
    
    silhouettes.append(metrics.silhouette_score(X, kmeans.labels_, metric='euclidean'))
    silhouettes_pre1.append(metrics.silhouette_score(X_preproc_1, kmeans_pre1.labels_, metric='euclidean'))
    
print("Silhouettes baseline: ", silhouettes)
print("Silhouettes pre-proc1: ", silhouettes_pre1)


# In[48]:


ns = range(1,21)

inertias_norm = inertias / np.linalg.norm(inertias)
inertias_pre1_norm = inertias_pre1 / np.linalg.norm(inertias_pre1)

plt.figure()
plt.plot(ns,inertias_norm,label="baseline")
plt.plot(ns,inertias_pre1_norm, label="pre-proc1")
plt.xlabel("Number of clusters")
plt.ylabel("SSE")
plt.title("K-mean")
plt.xticks(np.arange(0, 21, step=2))
plt.legend()
plt.show()

plt.figure()
plt.plot(ns,mse,label="baseline")
plt.plot(ns,mse_pre1, label="pre-proc1")
plt.xticks(np.arange(0, 21, step=2))
plt.xlabel("Number of clusters")
plt.ylabel("MSE")
plt.title("K-mean")
plt.legend()
plt.show()


plt.figure()
plt.plot(ns[1:],silhouettes,label="baseline")
plt.plot(ns[1:],silhouettes_pre1, label="pre-proc1")
plt.xticks(np.arange(0, 21, step=2))
plt.xlabel("Number of clusters")
plt.ylabel("Silhouettes")
plt.title("K-mean")
plt.legend()
plt.show()


# # PCA + K-means 

# In[50]:


pca_pre1 = sklearnPCA().fit(X_preproc_1)
plt.plot(np.cumsum(pca_pre1.explained_variance_ratio_))
plt.show()


# In[51]:


pca = sklearnPCA(n_components=35)
Y_pca = pca.fit_transform(X)
Y_pca_pre1 = pca.fit_transform(X_preproc_1)

inertias_pca, inertias_pre1_pca = [], []
ns = range(1,21)
for n in ns:
    kmeans = KMeans(n_clusters=n).fit(Y_pca)
    kmeans_pre1 = KMeans(n_clusters=n).fit(Y_pca_pre1)
    
    inertias_pca.append(kmeans.inertia_)
    inertias_pre1_pca.append(kmeans_pre1.inertia_)
    
print("Inertias baseline PCA: ", inertias_pca)
print("Inertias pre-proc1 PCA: ", inertias_pre1_pca)


# In[52]:


inertias_pca_norm = inertias_pca / np.linalg.norm(inertias_pca)
inertias_pre1_pca_norm = inertias_pre1_pca / np.linalg.norm(inertias_pre1_pca)

plt.plot(ns,inertias_norm,label="baseline")
#plt.plot(ns,inertias_pre1_norm, label="pre-proc1")
#plt.plot(ns,inertias_test_norm, label="test set")

plt.plot(ns,inertias_pca_norm,label="baseline w/ PCA")
#plt.plot(ns,inertias_pre1_pca_norm, label="pre-proc1 w/ PCA")
#plt.plot(ns,inertias_test_norm, label="test set")

plt.xlabel("num clusters")
plt.ylabel("SSE")
plt.title("K-mean after PCA")
plt.legend()
plt.xticks(np.arange(0, 21, step=2))
plt.show()


# # DBSCAN  !!DANGER -- KERNEL MORRE

# In[57]:


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


# In[63]:


plt.bar(ms,mses)
plt.show


# In[64]:


plt.plot(ms,mutual_infos,'o')
plt.show


# In[94]:


ris, mses, infos, silhouettes = [], [], [], []

epss = [0.05,0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5,0.55,0.6,0.65,0.7,0.75,0.8,0.85,0.9,0.95,1]
for e in epss:
    print(e)
    db = DBSCAN(eps=e,metric="cosine").fit(X)

    core_samples_mask = np.zeros_like(db.labels_, dtype=bool)
    core_samples_mask[db.core_sample_indices_] = True
    labels = db.labels_

    n_clusters = len(set(labels)) - (1 if -1 in labels else 0)

    ri = metrics.adjusted_rand_score(Y, labels)
    mse = metrics.mean_squared_error(Y, labels)
    info = metrics.mutual_info_score(Y, labels)
    sample = int(X.shape[0]*0.5)
    #silh = metrics.silhouette_score(X, labels, metric='euclidean',sample_size=sample)
    
    ris.append(ri)
    mses.append(mse)
    infos.append(info)
    silhouettes.append(silh)
    
    print("Adjusted Rand Index: %0.3f" % ri)
    print("MSE: %0.3f" % mse)
    print("Info Score :", info)
    print("silhouette_score: %0.3f" % silh)
    
print("Rand Indexes: ", ris)
print("MSEs: ", mses)
print("Info scores: ", infos)
print("Silhouette scores: ", silhouettes)


# In[95]:


#plt.plot(ns,inertias_norm,label="baseline")

plt.plot(epss,ris,label="pre-proc 1")
plt.xlabel("num clusters")
plt.ylabel("RI")
plt.title("DBSCAN Rand index")
plt.legend()
plt.show()

plt.plot(epss,mses,label="pre-proc 1")
plt.xlabel("num clusters")
plt.ylabel("MSE")
plt.title("DBSCAN Mean squared error")
plt.legend()
plt.show()

plt.plot(epss,infos,label="pre-proc 1")
plt.xlabel("num clusters")
plt.ylabel("Info score")
plt.title("DBSCAN Info score")
plt.legend()
plt.show()


# In[104]:


# create kmeans object
kmeans = KMeans(n_clusters=4)

# fit kmeans object to data
kmeans.fit(X)

# print location of clusters learned by kmeans object
print(kmeans.cluster_centers_)

# save new clusters for chart
y_km = kmeans.fit_predict(X)


# In[105]:


plt.scatter(X.iloc[y_km ==0,0], X.iloc[y_km == 0,1], s=100, c='m')
plt.scatter(X.iloc[y_km ==1,0], X.iloc[y_km == 1,1], s=100, c='c')
plt.scatter(X.iloc[y_km ==2,0], X.iloc[y_km == 2,1], s=100, c='y')
plt.scatter(X.iloc[y_km ==3,0], X.iloc[y_km == 3,1], s=100, c='tomato')


# # hierarchichal

# In[108]:


dendrogram = sch.dendrogram(sch.linkage(X, method='ward'))

# create clusters
hc = AgglomerativeClustering(n_clusters=4, affinity = 'euclidean', linkage = 'ward')

# save clusters for chart
y_hc = hc.fit_predict(X)


# In[103]:


plt.scatter(X.iloc[y_hc ==0,0], X.iloc[y_hc == 0,1], s=100, c='m')
plt.scatter(X.iloc[y_hc==1,0], X.iloc[y_hc == 1,1], s=100, c='c')
plt.scatter(X.iloc[y_hc ==2,0], X.iloc[y_hc == 2,1], s=100, c='y')
plt.scatter(X.iloc[y_hc ==3,0], X.iloc[y_hc == 3,1], s=100, c='tomato')


# In[ ]:




