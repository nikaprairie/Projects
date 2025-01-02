# -*- coding: utf-8 -*-
"""COMP550_A2.ipynb

Automatically generated by Colab.

Original file is located at
    https://colab.research.google.com/drive/1k3yNybsOLTLMcMnOtWlWZvntAvyOCQB2

### **Install Necessary Packages**
"""

import os
import subprocess
import sys

# Uninstall nltk
subprocess.check_call([sys.executable, '-m', 'pip', 'uninstall', 'nltk', '-y'])

# Reinstall nltk
subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'nltk'])
!pip install transformers torch

import nltk
import torch
import os
import numpy as np

from nltk.corpus import wordnet as wn
from nltk.wsd import lesk
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
from google.colab import drive
from transformers import BertTokenizer, BertModel
from scipy.spatial.distance import cosine

drive.mount('/content/drive')

"""### **Starter Code Provided**"""

import xml.etree.ElementTree as ET
import codecs
from nltk.corpus import wordnet as wn
import nltk

# Ensure necessary NLTK data is downloaded
nltk.download('wordnet')
nltk.download('omw-1.4')  # For additional WordNet data

class WSDInstance:
    def __init__(self, my_id, lemma, context, index):
        self.id = my_id         # id of the WSD instance
        self.lemma = lemma      # lemma of the word whose sense is to be resolved
        self.context = context  # lemma of all the words in the sentential context
        self.index = index      # index of lemma within the context
    def __str__(self):
        '''
        For printing purposes.
        '''
        return '%s\t%s\t%s\t%d' % (self.id, self.lemma, ' '.join(self.context), self.index)

def load_instances(f):
    '''
    Load two dictionaries of cases to perform WSD on. The structure that is returned is a dict, where
    the keys are the ids, and the values are instances of WSDInstance.
    '''
    tree = ET.parse(f)
    root = tree.getroot()

    dev_instances = {}
    test_instances = {}

    for text in root:
        if text.attrib['id'].startswith('d001'):
            instances = dev_instances
        else:
            instances = test_instances
        for sentence in text:
            # construct sentence context
            context = [to_ascii(el.attrib['lemma']) for el in sentence]
            for i, el in enumerate(sentence):
                if el.tag == 'instance':
                    my_id = el.attrib['id']
                    lemma = to_ascii(el.attrib['lemma'])
                    instances[my_id] = WSDInstance(my_id, lemma, context, i)
    return dev_instances, test_instances

def load_key(f):
    '''
    Load the solutions as dicts.
    Key is the id
    Value is the list of correct sense keys.
    '''
    dev_key = {}
    test_key = {}

    # Using context manager to ensure the file is properly closed after reading
    with open(f, 'r') as file:
        for line in file:
            if len(line.strip()) == 0:
                continue  # Skip empty lines

            # Safe parsing with error handling
            try:
                parts = line.strip().split(' ')
                doc = parts[0]
                my_id = parts[1]
                sense_keys = parts[2:]
                if doc == 'd001':
                    dev_key[my_id] = sense_keys
                else:
                    test_key[my_id] = sense_keys
            except ValueError as e:
                print(f"Error parsing line: {line}. Error: {e}")
                continue  # Skip lines that do not conform to expected format

    return dev_key, test_key

def to_ascii(s):
    return codecs.encode(s, 'ascii', 'ignore').decode('ascii')

def sense_key_to_synset(sense_key):

    try:
        lemma = wn.lemma_from_key(sense_key)
        synset = lemma.synset()
        return synset
    except:
        return None

def synset_to_sense_keys(synset):

    return [lemma.key() for lemma in synset.lemmas()]

"""### **Implementation of Lesk's Algorithm**"""

from collections import defaultdict
from nltk.corpus import wordnet as wn
from nltk.corpus import wordnet_ic
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import WordNetLemmatizer
from nltk.wsd import lesk
import random

# Required NLTK datasets
nltk.download('all')
brown_ic = wordnet_ic.ic('ic-brown.dat')

# Load instances and keys using existing functions
data_f = '/content/drive/My Drive/COMP 550/A2/multilingual-all-words.en.xml'
key_f = '/content/drive/My Drive/COMP 550/A2/wordnet.en.key'
dev_instances, test_instances = load_instances(data_f)
dev_key, test_key = load_key(key_f)

# Prepare text for processing
lemmatizer = WordNetLemmatizer()
stop_words = set(stopwords.words('english'))

def preprocess(text):
    tokens = word_tokenize(text.lower())
    tokens = [lemmatizer.lemmatize(word) for word in tokens if word.isalpha()]
    tokens = [word for word in tokens if word not in stop_words]
    return tokens

def get_wordnet_pos(treebank_tag):
    """
    Map POS tag from Treebank to WordNet POS.
    """
    tag = treebank_tag[0].upper()
    if tag == 'J':
        return wn.ADJ
    elif tag == 'N':
        return wn.NOUN
    elif tag == 'V':
        return wn.VERB
    elif tag == 'R':
        return wn.ADV
    else:
        return None

def extended_lesk(context_sentence, ambiguous_word, pos=None):

    context = set(context_sentence)
    if pos:
        synsets = wn.synsets(ambiguous_word, pos=pos)
    else:
        synsets = wn.synsets(ambiguous_word)
    if not synsets:
        return None
    max_overlap = 0
    best_sense = synsets[0]
    for sense in synsets:
        # Build the signature for each sense
        signature = set()
        # Include definition
        signature.update(word_tokenize(sense.definition()))
        # Include examples
        for example in sense.examples():
            signature.update(word_tokenize(example))
        # Include hypernyms and hyponyms definitions
        hypernyms = sense.hypernyms()
        hyponyms = sense.hyponyms()
        related_senses = hypernyms + hyponyms
        for related_sense in related_senses:
            signature.update(word_tokenize(related_sense.definition()))
            for example in related_sense.examples():
                signature.update(word_tokenize(example))
        # Preprocess signature
        signature = [lemmatizer.lemmatize(word.lower()) for word in signature if word.isalpha() and word.lower() not in stop_words]
        overlap = len(set(signature).intersection(context))
        if overlap > max_overlap:
            max_overlap = overlap
            best_sense = sense
    return best_sense

# Load pre-trained BERT model and tokenizer
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
bert_model = BertModel.from_pretrained('bert-base-uncased')

def get_bert_embedding(text):
    with torch.no_grad():
        inputs = tokenizer(text, return_tensors='pt', truncation=True)
        outputs = bert_model(**inputs)
        # Use the [CLS] token representation
        cls_embedding = outputs.last_hidden_state[0, 0, :].numpy()
    return cls_embedding

def evaluate_wsd(instances, keys, method='lesk'):
    correct = 0
    total = 0

    if method == 'yarowsky':
        # Implement Yarowsky's bootstrapping algorithm

        # Organize instances by ambiguous word (lemma)
        lemma_instances = defaultdict(list)
        for instance_id, instance in instances.items():
            lemma = instance.lemma.replace('_', ' ').lower()
            lemma_instances[lemma].append((instance_id, instance))

        # Initialize labeled data and feature counts
        labeled_data = {}
        feature_sense_counts = {}

        # Parameters
        SEED_SIZE = 2  # Number of seed examples per sense
        MAX_ITERATIONS = 5
        CONVERGENCE_THRESHOLD = 0.01

        # For each ambiguous word, apply Yarowsky's algorithm
        for lemma, lemma_inst_list in lemma_instances.items():
            # Get possible synsets for the lemma
            synsets = wn.synsets(lemma)
            if not synsets:
                continue  # Skip if no synsets are found

            # Initialize seeds
            random.shuffle(lemma_inst_list)
            seed_instances = lemma_inst_list[:SEED_SIZE * len(synsets)]
            unlabeled_instances = lemma_inst_list[SEED_SIZE * len(synsets):]

            # Assign initial senses to seeds
            initial_senses = []
            for i, (instance_id, instance) in enumerate(seed_instances):
                sense = synsets[i % len(synsets)]
                initial_senses.append((instance_id, instance, sense))
                # Update labeled data and feature counts
                tokens = preprocess(' '.join(instance.context))
                features = extract_features(tokens, lemma)
                for feature in features:
                    if feature not in feature_sense_counts:
                        feature_sense_counts[feature] = {}
                    if sense not in feature_sense_counts[feature]:
                        feature_sense_counts[feature][sense] = 0
                    feature_sense_counts[feature][sense] += 1
                labeled_data[instance_id] = sense

            # Bootstrapping loop
            for iteration in range(MAX_ITERATIONS):
                new_labels = []
                for instance_id, instance in unlabeled_instances:
                    if instance_id in labeled_data:
                        continue  # Skip if already labeled
                    tokens = preprocess(' '.join(instance.context))
                    features = extract_features(tokens, lemma)
                    sense_scores = {}
                    for feature in features:
                        if feature in feature_sense_counts:
                            for sense, count in feature_sense_counts[feature].items():
                                if sense not in sense_scores:
                                    sense_scores[sense] = 0
                                sense_scores[sense] += count
                    if sense_scores:
                        # Assign the sense with the highest score
                        predicted_sense = max(sense_scores, key=sense_scores.get)
                        new_labels.append((instance_id, instance, predicted_sense))
                if not new_labels:
                    break  # No new labels

                # Update labeled data and feature counts with new labels
                for instance_id, instance, sense in new_labels:
                    labeled_data[instance_id] = sense
                    tokens = preprocess(' '.join(instance.context))
                    features = extract_features(tokens, lemma)
                    for feature in features:
                        if feature not in feature_sense_counts:
                            feature_sense_counts[feature] = {}
                        if sense not in feature_sense_counts[feature]:
                            feature_sense_counts[feature][sense] = 0
                        feature_sense_counts[feature][sense] += 1

                # Update unlabeled instances
                unlabeled_instances = [(id_, inst) for id_, inst in unlabeled_instances if id_ not in labeled_data]

                # Check for convergence
                if len(new_labels) / len(lemma_inst_list) < CONVERGENCE_THRESHOLD:
                    break  # Convergence threshold reached

        # After bootstrapping, make predictions
        for instance_id, instance in instances.items():
            ambiguous_word = instance.lemma.replace('_', ' ').lower()
            tokens = preprocess(' '.join(instance.context))
            if instance_id in labeled_data:
                predicted_synset = labeled_data[instance_id]
            else:
                # Use feature counts to predict sense
                features = extract_features(tokens, ambiguous_word)
                sense_scores = {}
                for feature in features:
                    if feature in feature_sense_counts:
                        for sense, count in feature_sense_counts[feature].items():
                            if sense not in sense_scores:
                                sense_scores[sense] = 0
                            sense_scores[sense] += count
                if sense_scores:
                    predicted_synset = max(sense_scores, key=sense_scores.get)
                else:
                    predicted_synset = None  # Unable to predict
            # Evaluation
            if instance_id in keys:
                correct_keys = keys[instance_id]
                if predicted_synset:
                    # Get all sense keys associated with the predicted synset
                    predicted_keys = [lemma.key() for lemma in predicted_synset.lemmas()]
                    # Check if any of the predicted sense keys match the correct keys
                    if any(pred_key in correct_keys for pred_key in predicted_keys):
                        correct += 1
                total += 1
    else:

        for instance_id, instance in instances.items():
            context_sentence = ' '.join(instance.context)
            processed_context = preprocess(context_sentence)
            ambiguous_word = instance.lemma.replace('_', ' ')  # Handle multi-word expressions
            ambiguous_word = lemmatizer.lemmatize(ambiguous_word.lower())

            pos = None  # Default POS is None


            if hasattr(instance, 'pos') and instance.pos:
                pos = get_wordnet_pos(instance.pos)
            else:
                # Use NLTK's pos_tag to get the POS tag of the ambiguous word
                nltk_pos_tagged = nltk.pos_tag([ambiguous_word])
                nltk_pos = nltk_pos_tagged[0][1]
                pos = get_wordnet_pos(nltk_pos)

            if method == 'lesk':
                predicted_synset = lesk(processed_context, ambiguous_word, pos=pos)
            elif method == 'extended_lesk':
                predicted_synset = extended_lesk(processed_context, ambiguous_word, pos=pos)
            elif method == 'bert':

                inputs = tokenizer(' '.join(processed_context), return_tensors='pt', truncation=True)
                tokenized_text = tokenizer.convert_ids_to_tokens(inputs['input_ids'][0])

                ambiguous_tokens = tokenizer.tokenize(ambiguous_word)
                token_positions = []
                for i in range(len(tokenized_text) - len(ambiguous_tokens) + 1):
                    if tokenized_text[i:i+len(ambiguous_tokens)] == ambiguous_tokens:
                        token_positions = list(range(i, i+len(ambiguous_tokens)))
                        break
                if not token_positions:
                    predicted_synset = None
                else:
                    # Get embeddings for the ambiguous word tokens
                    with torch.no_grad():
                        outputs = bert_model(**inputs)
                    word_embeddings = outputs.last_hidden_state[0, token_positions, :]
                    word_embedding = word_embeddings.mean(dim=0).numpy()  # Average if multiple tokens

                    # Get sense embeddings
                    synsets = wn.synsets(ambiguous_word, pos=pos)
                    if not synsets:
                        predicted_synset = None
                    else:
                        max_similarity = -1
                        best_sense = None
                        for synset in synsets:
                            definition = synset.definition()
                            processed_definition = ' '.join(preprocess(definition))
                            sense_embedding = get_bert_embedding(processed_definition)
                            similarity = 1 - cosine(word_embedding, sense_embedding)
                            if np.isnan(similarity):
                                continue  # Skip if similarity is not a number
                            if similarity > max_similarity:
                                max_similarity = similarity
                                best_sense = synset
                        predicted_synset = best_sense
            else:  # Most frequent sense baseline
                synsets = wn.synsets(ambiguous_word, pos=pos)
                predicted_synset = synsets[0] if synsets else None

            # Evaluation
            if instance_id in keys:
                correct_keys = keys[instance_id]
                if predicted_synset:
                    # Get all sense keys associated with the predicted synset
                    predicted_keys = [lemma.key() for lemma in predicted_synset.lemmas()]
                    # Check if any of the predicted sense keys match the correct keys
                    if any(pred_key in correct_keys for pred_key in predicted_keys):
                        correct += 1
                total += 1

    accuracy = correct / total if total > 0 else 0
    print(f"Method: {method}, Total Evaluated: {total}, Correct Matches: {correct}, Accuracy: {accuracy:.4f}")
    return accuracy

def extract_features(tokens, ambiguous_word):
    """
    Extract features from tokens for Yarowsky's algorithm.
    Features include:
    - Words immediately before and after the ambiguous word.
    - Bigrams formed with the ambiguous word.
    """
    features = set()
    for i, word in enumerate(tokens):
        if word == ambiguous_word:
            # Unigram features: words before and after
            if i > 0:
                features.add(f"prev_{tokens[i - 1]}")
            if i < len(tokens) - 1:
                features.add(f"next_{tokens[i + 1]}")
            # Bigram features
            if i > 0 and i < len(tokens) - 1:
                features.add(f"bigram_{tokens[i - 1]}_{tokens[i + 1]}")
    return features

if __name__ == '__main__':
    data_f = '/content/drive/My Drive/COMP 550/A2/multilingual-all-words.en.xml'
    key_f = '/content/drive/My Drive/COMP 550/A2/wordnet.en.key'
    dev_instances, test_instances = load_instances(data_f)
    dev_key, test_key = load_key(key_f)

    # IMPORTANT: keys contain fewer entries than the instances; need to remove instances without keys
    dev_instances = {k:v for (k,v) in dev_instances.items() if k in dev_key}
    test_instances = {k:v for (k,v) in test_instances.items() if k in test_key}

    # Now you can proceed with evaluation
    # For example, to get the correct synsets for a given instance:
    for instance_id, instance in dev_instances.items():
        correct_sense_keys = dev_key[instance_id]
        correct_synsets = [sense_key_to_synset(key) for key in correct_sense_keys]
        # Now you have the correct synsets for comparison
        # Continue with your WSD algorithm and evaluation


    # Evaluate using all methods
    print("Evaluating Most Frequent Sense Baseline:")
    baseline_accuracy = evaluate_wsd(dev_instances, dev_key, method='baseline')
    print("\nEvaluating NLTK's Lesk Algorithm:")
    lesk_accuracy = evaluate_wsd(dev_instances, dev_key, method='lesk')
    print("\nEvaluating Extended Lesk Algorithm:")
    extended_lesk_accuracy = evaluate_wsd(dev_instances, dev_key, method='extended_lesk')
    print("\nEvaluating BERT-based WSD:")
    bert_accuracy = evaluate_wsd(dev_instances, dev_key, method='bert')
    print("\nEvaluating Yarowsky's Algorithm:")
    yarowsky_accuracy = evaluate_wsd(dev_instances, dev_key, method='yarowsky')

    print(f'\nBaseline Accuracy on Dev: {baseline_accuracy:.4f}')
    print(f'Lesk Accuracy on Dev: {lesk_accuracy:.4f}')
    print(f'Extended Lesk Accuracy on Dev: {extended_lesk_accuracy:.4f}')
    print(f'BERT-based WSD Accuracy on Dev: {bert_accuracy:.4f}')
    print(f"Yarowsky's Algorithm Accuracy on Dev: {yarowsky_accuracy:.4f}")

    print("")


    # Evaluate using all methods
    print("Evaluating Most Frequent Sense Baseline:")
    baseline_accuracy = evaluate_wsd(test_instances, test_key, method='baseline')
    print("\nEvaluating NLTK's Lesk Algorithm:")
    lesk_accuracy = evaluate_wsd(test_instances, test_key, method='lesk')
    print("\nEvaluating Extended Lesk Algorithm:")
    extended_lesk_accuracy = evaluate_wsd(test_instances, test_key, method='extended_lesk')
    print("\nEvaluating BERT-based WSD:")
    bert_accuracy = evaluate_wsd(test_instances, test_key, method='bert')
    print("\nEvaluating Yarowsky's Algorithm:")
    yarowsky_accuracy = evaluate_wsd(test_instances, test_key, method='yarowsky')

    print(f'\nBaseline Accuracy on Test: {baseline_accuracy:.4f}')
    print(f'Lesk Accuracy on Test: {lesk_accuracy:.4f}')
    print(f'Extended Lesk Accuracy on Test: {extended_lesk_accuracy:.4f}')
    print(f'BERT-based WSD Accuracy on Test: {bert_accuracy:.4f}')
    print(f"Yarowsky's Algorithm Accuracy on Test: {yarowsky_accuracy:.4f}")