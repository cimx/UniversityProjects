#!/usr/bin/env python
# coding: utf-8

# # Statistical Analysis
# 
# 
# ### Imports

# In[4]:


import pandas as pd
import numpy as np
from sklearn.preprocessing import label_binarize
from sklearn.feature_selection import SelectKBest
from sklearn.feature_selection import chi2
import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')
from pylab import rcParams
from pandas.plotting import scatter_matrix
from sklearn.feature_selection import RFE
from sklearn.linear_model import LogisticRegression
from sklearn import preprocessing
from matplotlib.axes import Axes

import warnings
warnings.filterwarnings('ignore')


# In[20]:


def remove_missing_values(dataset, feature_names, class_name = None):
    dataset_preprocessed = dataset.copy()
    
    if class_name == None:
        for feature in feature_names:
            dataset_preprocessed[feature] = dataset_preprocessed.transform(lambda x: x.fillna(x.mean()))[feature]
    else:
        for feature in feature_names:
            dataset_preprocessed[feature] = dataset_preprocessed.groupby(class_name).transform(lambda x: x.fillna(x.mean()))[feature]

    return dataset_preprocessed


# # 1st dataset - aps failure
# 
# ### Load data set

# In[5]:


#Load first data set - ps_failure_training_set
first_train = pd.read_csv("datasets/first/aps_failure_training_set.csv",",", skiprows=20, na_values = "na", low_memory = False).replace(to_replace='neg', value=0).replace(to_replace='pos', value=1).replace(to_replace='NaN', value=-1)
#first_train = first_train.fillna(0)


# In[11]:


cols = first_train.columns.values[1:]
class_name = "class"
#first_train = remove_missing_values(first_train, cols, class_name)

y = first_train["class"]

## CORRIGIR, labels
target_count = y.value_counts()
target_count.plot(kind='bar', title='Count (target)');
inactive_count = target_count[0]
active_count = target_count[1]


# ### Univariate feature selection - SelectKBest

# In[61]:


#Normalize data
#X_normalized = normalize(X, norm='l2')
#X = pd.DataFrame(data=X_normalized, index=second.index, columns=second.columns[:-7])

X = first_train.iloc[:,1:]
Y = first_train["class"]
labels = pd.unique(Y)
#Y = label_binarize(Y, classes=labels)

# Feature extraction
K = 5

model = SelectKBest(score_func=chi2, k=K)
X_new = model.fit_transform(X,Y)
new_columns = X.columns[model.get_support()]
print(X_new.shape)
print("- Features with  information gain:\n", new_columns)


# In[114]:


pearson_values = first_train.corr()
count = 0
for v in pearson_values:
    for i in pearson_values:
        if(pearson_values[v][i]>=0.95 and pearson_values[v][i]!=1):
            print(v,"-",i,": ",pearson_values[v][i])
            count+=1
print(count)


# ### Analyze the data
# 
# 
#     std - sample standard deviation
#     50% = median

# In[59]:


'''
#selected = first_train.loc[:,['ac_000','an_000','bb_000','bu_000','bv_000','bx_000','cc_000','ci_000','cq_000','dq_000']]

selected = first_train.loc[:,new_columns]
selected_features = selected.columns.tolist()
print(selected_features)
#selected = pd.concat(selected,axis=1)
selected = pd.concat([Y,selected],axis=1)

selected.describe(include=selected_features)
'''
#selected = first_train.loc[:,['ac_000','an_000','bb_000','bu_000','bv_000','bx_000','cc_000','ci_000','cq_000','dq_000']]

selected = X.loc[:,new_columns]
#selected = pd.concat(selected,axis=1)
selected = pd.concat([Y,selected],axis=1)

selected.describe()


#     Variance:

# In[60]:


variance = selected.var(axis=0)
print(variance)


# #### Distribution of each featured attribute
# 
#     'dq_000' has a lot of outliers
# 
#     apart from 'ac_000' they all have outliers

# In[94]:


rcParams['figure.figsize'] = 19, 10

color = dict(boxes='DarkGreen', whiskers='DarkOrange',medians='DarkBlue', caps='Gray')

#pd.options.display.mpl_style = 'default'
p = selected.plot.box(color=color, sym='r+')


# #### Distriburion
#     1. distribution of selected features
#     2. distribution of selected features grouped by 'class'
# No Normal distribution

# In[63]:


p = selected.groupby('class').hist()


# In[42]:


#p = scatter_matrix(selected, alpha=0.2, figsize=(6, 6), diagonal='kde')


# # 2nd dataset - green, hinselmann, schiller
# 
# ### Load data sets

# In[43]:


#Load second data set - ps_failure_training_set
green = pd.read_csv("datasets/second/green.csv",",")
hinselmann = pd.read_csv("datasets/second/hinselmann.csv",",")
schiller = pd.read_csv("datasets/second/schiller.csv",",")

second = pd.concat([green,hinselmann,schiller])
second

#second.groupby("consensus").describe()


# In[44]:


X = second.iloc[:,:-7]
#Y = second["experts::0","experts::1","experts::2","experts::3","experts::4","experts::5","consensus"]
Y0 = second["experts::0"]
Y1 = second["experts::1"]
Y2 = second["experts::2"]
Y3 = second["experts::3"]
Y4 = second["experts::4"]
Y5 = second["experts::5"]
Y6 = second["consensus"]


#Normalize data
#X_normalized = normalize(X, norm='l2')
#X = pd.DataFrame(data=X_normalized, index=second.index, columns=second.columns[:-7])

for attr in X:
    X[attr] = second.transform(lambda x: x + abs(x.min()))[attr]

# Feature extraction
K = 5

model = SelectKBest(score_func=chi2, k=K)
X_new = model.fit_transform(X,Y6)
new_columns = X.columns[model.get_support()]
print(X_new.shape)
print("- Features with  information gain:\n", new_columns)


# In[113]:


pearson_values = second.corr()
count = 0
for v in pearson_values:
    for i in pearson_values:
        if(pearson_values[v][i]>=0.95 and pearson_values[v][i]!=1):
            print(v,"-",i,": ",pearson_values[v][i])
            count+=1
print(count)
pearson_values


# ### Analyze the data
# 
# 
#     std - sample standard deviation
#     50% = median

# In[45]:


#selected = first_train.loc[:,['cervix_area', 'speculum_artifacts_area', 'hsv_cervix_h_std','hsv_total_h_mean', 'hsv_total_h_std', 'fit_cervix_bbox_total','fit_circle_rate', 'fit_circle_total', 'dist_to_center_cervix','dist_to_center_os']]

selected = second.loc[:,new_columns]
#selected = pd.concat(selected,axis=1)
selected = pd.concat([Y6,selected],axis=1)

selected.describe()


#     Variance:

# In[46]:


variance = selected.var(axis=0)
print(variance)


# #### Distribution of each featured attribute
# 
#     - speculum_artifactsa_area has multiple outliers

# In[55]:


rcParams['figure.figsize'] = 20, 10

color = dict(boxes='DarkGreen', whiskers='DarkOrange',medians='DarkBlue', caps='Gray')

#pd.options.display.mpl_style = 'default'
p = selected.plot.box(color=color, sym='r+')


# #### Distriburion
#     1. distribution of selected features
#     2. distribution of selected features grouped by 'class'
#     
#         1. No normal distribution
#         2. Closest to Normal distribution - cervix area? 

# In[57]:


rcParams['figure.figsize'] = 10, 10
p = selected.groupby('consensus').hist()


# In[ ]:




