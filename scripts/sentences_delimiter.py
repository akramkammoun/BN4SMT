import nltk
import sys
import codecs

##sys.argv[0] = file name
fileIn = sys.argv[1]
fileOut = sys.argv[2]
dataPath = sys.argv[3]
##'tokenizers/punkt/english.pickle'
tokenModulePath = sys.argv[4]


nltk.data.path += [dataPath]
textInFlux = open(fileIn,"r")
#textInFlux = codecs.open(fileIn, 'r', encoding='utf8')
textIn = textInFlux.read()


textOutFlux = open(fileOut,"w")
#textOutFlux = codecs.open(fileOut, 'w', encoding='utf8')

sent_tokenizer=nltk.data.load(tokenModulePath)
sents = sent_tokenizer.tokenize(textIn)

for ligne in sents:
	textOutFlux.write(ligne + "\n")
