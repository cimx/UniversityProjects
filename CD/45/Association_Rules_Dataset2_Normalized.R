###### Rafael Belchior, Inês Xavier and Duarte Galvão
#####
###
##


library (arules)
library(arulesSequences)
library(BBmisc)

setwd("C:\\Users\\Rafael Belchior\\OneDrive\\Mestrado\\2A2S\\CD\\Labs\\CD-Grupo45\\Project")

dataset2 <- read.csv('second.csv', header=TRUE)
dataset2 <- dataset2[, -c(63:69)]

dataset2_norm = dataset2 
dataset2_disc = dataset2
dataset2_disc_norm = dataset2

dataset2_norm <- normalize(dataset2, method = "range", range = c(0, 1), margin = 1L, on.constant = "quiet")
View(dataset2_norm)

for(i in 1:62) dataset2_disc_norm[,i] <- discretize(dataset2_norm[,i],  method = "cluster", breaks=5)
View(dataset2_disc_norm)


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
  rules <-apriori(dataset2_disc_norm,aParam)
  tamanho = length(rules)
  numberRules[i] = tamanho 
  print(numberRules)
  #top10 = head(sort(rules, decreasing = TRUE, by="lift"),10)
  #summary(top10)
  #top10 = head(sort(rules, decreasing = TRUE, by="lift"),10)
  #lifts[i] <- top10
  top10 = head(sort(rules, decreasing = TRUE, by="lift"),10)
  s <- summary(top10)
  s@quality[16][1]
  v <- as.numeric(gsub('.*:', '', s@quality[16][1]))
  lifts[i] <- v
}


print(numberRules)
print(supports)
lifts[10] = 0
print(lifts)


# Define the cars vector with 5 values
# Graph cars using blue points overlayed by a line 
plot(supports, numberRules, type="o", col="#8b0000", xlab="Support", ylab="Number of rules")
plot(supports, lifts, type="o", col="#8b0000", xlab="Support", ylab="Top 10 lift")



