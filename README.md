# Java_MI
Java code for computing normalized mutual information

compute_mutual_information(Array_a, Array_b)

input: Array_a, Array_b
return double mutual information

compute_normalized_mutual_information(Array_a, Array_b)
input: Array_a, Array_b
return double normalized mutual information [0,1]

normalized_mutual_information = mutual_information / Math.sqrt(Entropy(aArray) * Entropy(bArray))

compute_entropy(Array)
input: Array
return entropy

