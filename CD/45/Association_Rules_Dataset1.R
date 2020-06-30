###### Rafael Belchior, Inês Xavier and Duarte Galvão
#####
###
##


library (arules)
library(arulesSequences)

setwd("C:\\Users\\Rafael Belchior\\OneDrive\\Mestrado\\2A2S\\CD\\Labs\\CD-Grupo45\\Project")

dataset1 <- read.csv('first.csv', header=TRUE, skip = 20, na.strings=c("na"))
dataset1["class"] <- NULL
View(dataset1)

dataset1_disc = dataset1
dataset1_norm = dataset1

dataset1_norm <- normalize(dataset1, method = "range", range = c(0, 1), margin = 1L, on.constant = "quiet")
View(dataset1_norm)

ncol(dataset1_norm)
dataset1_disc = dataset1_norm[, -which(colMeans(is.na(dataset1_norm)) > 0.1)]
ncol(dataset1_norm_cols_rem)
View(dataset1_norm_cols_rem)

for(i in 1:170) dataset1_disc[,i] <- discretize(dataset1[,i],  method = "frequency", breaks=5)
#View(dataset1_disc)


numberRules <- 1:10
supports <- 1:10
lifts <- 1:10
for (i in 1:10) {
  print("Iteration number:")
  print(i)
  print("suporte")
  print(i/10)
  supports[i] <- i/10
  aParam  = new("APparameter", "confidence" = 0.90, "maxtime" = 20, "support" = i/10, "minlen"= 1, "maxlen"=3) 
  aParam@target ="rules"
  rules <-apriori(dataset1_disc,aParam)
  tamanho = length(rules)
  numberRules[i] = tamanho 
  print(numberRules)
  #top10 = head(sort(rules, decreasing = TRUE, by="lift"),10)
  #summary(top10)
  #top10 = head(sort(rules, decreasing = TRUE, by="lift"),10)
  #lifts[i] <- top10
  top10 = head(sort(rules, decreasing = TRUE, by="lift"),10)
  s <- summary(top10)
  str(s)
  s@quality[16][1]
  v <- as.numeric(gsub('.*:', '', s@quality[16][1]))
  lifts[i] <- v
  
}
#interestMeasure(rules, c("support", "chiSquare", "confidence", "conviction","cosine", "coverage", "leverage", "lift", "oddsRatio"), dataset2_disc)

print(numberRules)
print(supports)
lifts[10] = 0
print(lifts)


# Define the cars vector with 5 values
# Graph cars using blue points overlayed by a line 
plot(supports, numberRules, type="o", col="orange", xlab="Support", ylab="Number of rules")
plot(supports, lifts, type="o", col="orange", xlab="Support", ylab="Top 10 lift")



