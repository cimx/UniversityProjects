###### Rafael Belchior, In??s Xavier and Duarte Galv??o
#####
###
##

## This file mines association rules with respect to the Coloposcopy dataset.
## There are two experiments: 




library (arules)
library(arulesSequences)
library(BBmisc)
library(dplyr)
library(openair)
library(plyr)
library(ppls)
setwd("C:\\Users\\Rafael Belchior\\OneDrive\\Mestrado\\2A2S\\CD\\Labs\\CD-Grupo45\\Project")
dataset2 <- read.csv('second.csv', header=TRUE, stringsAsFactors=FALSE )
#dataset2$experts..0 <- as.factor(dataset2$experts..0)
#dataset2$experts..1 <- as.factor(dataset2$experts..1)
#dataset2$experts..2 <- as.factor(dataset2$experts..2)
#dataset2$experts..3 <- as.factor(dataset2$experts..3)
#dataset2$experts..4 <- as.factor(dataset2$experts..4)
#dataset2$experts..5 <- as.factor(dataset2$experts..5)
#dataset2$consensus <- as.factor(dataset2$consensus)


is.factor(dataset2$experts..0)
summary(dataset2)
#View(dataset2)



#c0 = count(dataset2 , 'experts..0')
#print(c0$freq)

#percetagem deles que vota num e outro

dataset2_norm <- normalize(dataset2, method = "range", range = c(0, 1))
summary(dataset2_norm)

f <- factor(c(0,1)) 
fo <- ordered(f) 
#fo
#max(fo)
#dataset2[,1:62]
is.factor(dataset2$consensus)
for(i in 1:62) dataset2_disc[,i] <- discretize(dataset2[,i],  method = "interval", breaks = 5)
for(i in 62:69) dataset2_disc[,i] <- discretize(dataset2[,i],  method = "interval", breaks = 2)

summary(dataset2_disc)
colnames(dataset2_disc)

aParam  = new("APparameter","target" = "rules", "confidence" = 0.90, "support" =0.1, "minlen"= 1, "maxlen"=2) 
print(aParam)

Experiment1 <-apriori(dataset2_disc,aParam)
length(Experiment1)
summary(Experiment1)

#Genearl
#top10Lift = head(sort(Experiment1, decreasing = TRUE, by="lift"),10)
#inspect(top10Lift)

expert0 <- subset(Experiment1, subset = lhs %pin% "experts..0")
length(expert0)
top10expert0 = head(sort(expert0, decreasing = TRUE, by="lift"),10)

inspect(top10expert0)

#####Expert 1
expert1 <- apriori(dataset2_disc,aParam, appearance = list(lhs = c("experts..1=[0.5,1]"), rhs = c("consensus=[0.5,1]")))
length(expert1)
inspect(expert1)

#####Expert 2
expert2 <- apriori(dataset2_disc,aParam, appearance = list(lhs = c("experts..2=[0.5,1]"), rhs = c("consensus=[0.5,1]")))
length(expert2)
inspect(expert2)

#####Expert 3
expert3 <- apriori(dataset2_disc,aParam, appearance = list(lhs = c("experts..3=[0.5,1]"), rhs = c("consensus=[0.5,1]")))
length(expert3)
inspect(expert3)

#####Expert 4
expert4 <- apriori(dataset2_disc,aParam, appearance = list(lhs = c("experts..4=[0.5,1]"), rhs = c("consensus=[0.5,1]")))
length(expert4)
inspect(expert4)

#####Expert 5
expert5 <- apriori(dataset2_disc,aParam, appearance = list(lhs = c("experts..5=[0.5,1]"), rhs = c("consensus=[0.5,1]")))
length(expert5)
inspect(expert5)


## Results experiment 1
# expert 4 dones't have rules for our parameters
confidence <- c(0.9617486,0.9580838,0.9009434,0.9403974)
lift <- c(1.277879,1.27301,1.197087,1.249509)
confidence_n <- normalize.vector(confidence)
lift_n <- normalize.vector(lift)

#We conclude that Expert 1 is the most accurate/persuasive expert 
#############################################
################################ Experiment 2
aParam  = new("APparameter","target" = "rules", "confidence" = 0.80, "support" =0.01, "minlen"= 1, "maxlen"=3) 
print(aParam)

#####All experts
experts <- apriori(dataset2_disc,aParam, appearance = list(lhs = c("experts..0=[0.5,1]","experts..1=[0.5,1]","experts..2=[0.5,1]","experts..3=[0.5,1]","experts..4=[0.5,1]","experts..5=[0.5,1]"), rhs = c("consensus=[0.5,1]")))
length(experts)
inspect(experts)

top3C= head(sort(experts, decreasing = TRUE, by="confidence"),3)
inspect(top3C)

top3L= head(sort(experts, decreasing = TRUE, by="lift"),3)
inspect(top3L)
