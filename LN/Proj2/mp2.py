import nltk
import os, time
import string, re, math, sys
import numpy as np
from io import open
from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer

def GetShortestDistanceIndex(vector, train_vectors):
    short_index = 0
    short_distance = 0

    for index in range(0, len(train_vectors)):
        distance = cosine_similarity(train_vectors[index],vector)
        if(distance > short_distance):

            short_distance = distance
            short_index = index        
    return short_index

def cosine_similarity(vector1, vector2):
    dot_product = sum(p*q for p,q in zip(vector1, vector2))
    magnitude = math.sqrt(sum([val**2 for val in vector1])) * math.sqrt(sum([val**2 for val in vector2]))
    if not magnitude:
        return 0
    return dot_product/magnitude

def getLabel(questions_file,questions_file_train):
    test_questions_list, test_labels_list = questions_file, ["" for x in range(0,len(questions_file))]
    train_vector_list = []

    train_questions_list, train_labels_list = processTrainDocument(questions_file_train)

    for index in range(0, len(questions_file)):
        train_vector_list, test_vector = tfidf(test_questions_list[index], train_questions_list)    
        short_index = GetShortestDistanceIndex(test_vector,train_vector_list)
        test_labels_list[index] = train_labels_list[short_index]
        print(test_labels_list[index])

def getBestLabel(train_labels_list, short_index):
    myMap = {}
    best_index = short_index[0]
    for n in short_index:
        if train_labels_list[n] in myMap: 
            myMap[train_labels_list[n]] += 1
        else: 
            myMap[train_labels_list[n]] = 1

        if myMap[train_labels_list[n]] > myMap[train_labels_list[best_index]]:
            best_index = n
    return best_index

def processTrainDocument(questions_file_train) :
    train_questions_list, train_labels_list = [], []
    for elem in questions_file_train:
        separate = elem.split(" ",1)
        question = separate[1]
        
        question = processQuestion(question)
        
        train_questions_list.append(question)
        train_labels_list.append(separate[0])
    return train_questions_list, train_labels_list

def tfidf(current_question, train_questions_list):   
    current_question = processQuestion(current_question)
    
    train_questions_list.append(current_question)
    
    vectorizer = TfidfVectorizer(ngram_range=(2,3),stop_words= 'english')

    X = vectorizer.fit_transform(train_questions_list)
    train_questions_list.pop()
    train_vector_list = X = X.toarray()
    
    for n in range(0, len(X)):
        train_vector_list[n] = np.true_divide(X[n],np.sum(X[n]))
    question_vector = train_vector_list[len(train_vector_list)-1]
    train_vector_list =  train_vector_list[0:len(train_vector_list)-1]
    return train_vector_list , question_vector

def processQuestion(question):
    question = re.sub('\n', '', re.sub('\t', '', question))
    question = re.sub('[%s]' % re.escape(string.punctuation), '', question)

    try:
        maketrans = ''.maketrans
    except AttributeError:
        from string import maketrans
    table = maketrans({}.fromkeys(string.punctuation))

    found=[]
    token_file = list(open(os.path.abspath('recursos/list_people.txt'), 'r', encoding="utf-8"))
    for line in token_file:
        line = line.replace('\n','')
        line = line.translate(table)
        if " "+line+" " in question:
            found.append(line)
        elif " "+line in question and question.index(line)+len(line)==len(question):
            found.append(line)
        elif line+" " in question and question.index(line)==0:
            found.append(line)
    if len(found)>0 : 
        replace = max(found, key=len)
        question = question.replace(replace, "person")
        found=[]
    token_file = list(open(os.path.abspath('recursos/list_genres.txt'), 'r'))
    for line in token_file:
        line = line.replace('\n','')
        line = line.translate(table)
        if " "+line+" " in question:
            found.append(line)
        elif " "+line in question and question.index(line)+len(line)==len(question):
            found.append(line)
        elif line+" " in question and question.index(line)==0:
            found.append(line)
    if len(found)>0 : 
        replace = max(found, key=len)
        question = question.replace(replace, "genre")
        found=[]
    token_file = list(open(os.path.abspath('recursos/list_jobs.txt'), 'r'))
    for line in token_file:
        line = line.replace('\n','')
        line = line.translate(table)
        if " "+line+" " in question:
            found.append(line)
        elif " "+line in question and question.index(line)+len(line)==len(question):
            found.append(line)
        elif line+" " in question and question.index(line)==0:
            found.append(line)
    if len(found)>0 : 
        replace = max(found, key=len)
        question = question.replace(replace, "job")
        found=[]
    token_file = list(open(os.path.abspath('recursos/list_companies.txt'), 'r', encoding="utf-8"))
    for line in token_file:
        line = line.replace('\n','')
        line = line.translate(table)
        if " "+line+" " in question:
            found.append(line)
        elif " "+line in question and question.index(line)+len(line)==len(question):
            found.append(line)
        elif line+" " in question and question.index(line)==0:
            found.append(line)
    if len(found)>0 : 
        replace = max(found, key=len)
        question = question.replace(replace, "company")
        found=[]
    token_file = list(open(os.path.abspath('recursos/list_movies.txt'), 'r'))
    for line in token_file:
        line = line.replace('\n','')
        line = line.translate(table)
        if " "+line+" " in question:
            found.append(line)
        elif " "+line in question and question.index(line)+len(line)==len(question):
            found.append(line)
        elif line+" " in question and question.index(line)==0:
            found.append(line)
    if len(found)>0 : 
        replace = max(found, key=len)
        question = question.replace(replace, "movie")
        found=[]
    token_file = list(open(os.path.abspath('recursos/list_characters.txt'), 'r', encoding="utf-8"))
    for line in token_file:
        line = line.replace('\n','')
        line = line.translate(table)
        if " "+line+" " in question:
            found.append(line)
        elif " "+line in question and question.index(line)+len(line)==len(question):
            found.append(line)
        elif line+" " in question and question.index(line)==0 and line.strip()!="":
            found.append(line)
    if len(found)>0 : 
        replace = max(found, key=len)
        question = question.replace(replace, "character")
        found=[]
    return question.lower()
    
if __name__ == "__main__": 
    file_lines = list(open(os.path.abspath(sys.argv[1]), 'r'))
    file_lines_test = list(open(os.path.abspath(sys.argv[2]), 'r'))

    getLabel(file_lines_test,file_lines)