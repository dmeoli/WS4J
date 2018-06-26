# WordNet Similarity for Java [![Build Status](https://travis-ci.org/DonatoMeoli/WS4J.svg?branch=master)](https://travis-ci.org/DonatoMeoli/WS4J) [![Release](https://jitpack.io/v/DonatoMeoli/WS4J.svg)](https://jitpack.io/#DonatoMeoli/WS4J)

WS4J provides a pure Java API for several published semantic relatedness/similarity algorithms for, in theory, any 
WordNet instance. You can immediately use WS4J on Princeton's English WordNet 3.0 lexical database through 
[MIT Java WordNet Interface 2.4.0](https://projects.csail.mit.edu/jwi/), which is the fastest Java library for 
interfacing to WordNet.

The codebase is mostly a Java re-implementation of [WordNet::Similarity](http://wn-similarity.sourceforge.net/) 
written in Perl, using the same data files as seen in src/main/resources, with some test cases for verifying the same 
logic. WS4J designed to be thread safe.

## Relatedness/Similarity Algorithms 

The semantic relatedness/similarity metrics available are:

 - [HSO](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/hso.pm): 
 [Hirst & St-Onge, 1998](https://scholar.google.com/scholar?q=Lexical+chains+as+representations+of+context+for+the+detection+and+correction+of+malapropisms) - 
 The Hirst & St-Onge measure is based on an idea that two lexicalized concepts are semantically close if their WordNet 
 synsets are connected by a path that is not too long and that "does not change direction too often":
 
 HSO(s1, s2) = const_C - path_length(s1, s2) - const_k * num_of_changes_of_directions(s1, s2);
 
 - [LCH](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/lch.pm): 
 [Leacock & Chodorow, 1998](https://scholar.google.com/scholar?q=Combining+local+context+and+WordNet+similarity+for+word+sense+identification) - 
 The Leacock & Chodorow measure relies on the length of the shortest path between two synsets for their measure of similarity:
 
 LCH(s1, s2) = -Math.log_e(LCS(s1, s2).length / (2 * max_depth(pos)));
 
 - [LESK](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/lesk.pm): 
 [Banerjee & Pedersen, 2002](https://scholar.google.com/scholar?q=An+Adapted+Lesk+Algorithm+for+Word+Sense+Disambiguation+Using+WordNet) - 
 Lesk (1985) proposed that the relatedness of two words is proportional to to the extent of overlaps of their dictionary 
 definitions. This Lesk measure is based on adapted Lesk from Banerjee and Pedersen (2002) extended this notion to use 
 WordNet as the dictionary for the word definitions:
 
 LESK(s1, s2) = sum_{s1' in linked(s1), s2' in linked(s2)}(overlap(s1'.definition, s2'.definition));
 
 - [WUP](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/wup.pm): 
 [Wu & Palmer, 1994](https://scholar.google.com/scholar?q=Verb+semantics+and+lexical+selection) - The Wu & Palmer 
 measure calculates relatedness by considering the depths of the two synsets in the WordNet taxonomies, along with the 
 depth of the LCS:
 
 WUP(s1, s2) = 2 * dLCS.depth / (min_{dlcs in dLCS}(s1.depth - dlcs.depth)) + min_{dlcs in dLCS}(s2.depth - dlcs.depth)), 
 where dLCS(s1, s2) = argmax_{lcs in LCS(s1, s2)}(lcs.depth);
 
 - [RES](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/res.pm): 
 [Resnik, 1995](https://scholar.google.com/scholar?q=Using+information+content+to+evaluate+semantic+similarity+in+a+taxonomy) - 
 Resnik defined the similarity between two synsets to be the information content of their lowest super-ordinate (most 
 specific common subsumer):
 
 RES(s1, s2) = IC(LCS(s1, s2));
 
 - [PATH](http://search.cpan.org/~tpederse/WordNet-Similarity/lib/WordNet/Similarity/path.pm) - The Path measure 
 computes the semantic relatedness of word senses by counting the number of nodes along the shortest path between the 
 senses in the 'is-a' hierarchies of WordNet:
 
 PATH(s1, s2) = 1 / path_length(s1, s2);
 
 - [JCN](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/jcn.pm): 
 [Jiang & Conrath, 1997](https://scholar.google.com/scholar?q=Semantic+similarity+based+on+corpus+statistics+and+lexical+taxonomy) - 
 The Jiang & Conrath measure uses the notion of information content, but in the form of the conditional probability of 
 encountering an instance of a child-synset given an instance of a parent synset: 
 
 JCN(s1, s2) = 1 / jcn_distance where jcn_distance(s1, s2) = IC(s1) + IC(s2) - 2 * IC(LCS(s1, s2)); when it's 0, 
 jcn_distance(s1, s2) = -Math.log_e((freq(LCS(s1, s2).root) - 0.01) / freq(LCS(s1, s2).root)) so that we can have a 
 non-zero distance which results in infinite similarity;
 
 - [LIN](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/lin.pm): 
 [Lin, 1998](https://scholar.google.com/scholar?q=An+information-theoretic+definition+of+similarity) - The Lin measure 
 idea is similar to JCN with small modification:
 
 LIN(s1, s2) = 2 * IC(LCS(s1, s2) / (IC(s1) + IC(s2)).

The descriptions above are extracted either from each paper or from 
[WordNet-Similarity CPAN documentation](http://search.cpan.org/dist/WordNet-Similarity/).

## Prerequisites

By default, requirement for compilation are:

 - JDK 8+
 - Maven

Any WordNet instance can be used in WS4J if it implements the ILexicalDatabase interface.

## Built with Maven

To customize WS4J, edit:

  `src/main/config/WS4J.conf`

To create a jar file with dependencies including resource and config files:

```
$ mvn install assembly:single
```

## Using WS4J

Then start playing with the facade WS4J API:

  `src/main/java/edu/uniba/di/lacam/kdde/donato/meoli/ws4j/WS4J.java`

and a simple demo class:

  `src/main/java/edu/uniba/di/lacam/kdde/donato/meoli/ws4j/demo/SimilarityCalculationDemo.java`

When using the WS4J jar package from other projects, make sure to also include depending libraries. In maven's pom file, 
these repository and dependency can be written such as: 

    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>com.github.DonatoMeoli</groupId>
            <artifactId>WS4J</artifactId>
            <version>master</version>
        </dependency>
    </dependencies>

## Running the tests

To run JUnit test cases:
   
```
$ mvn test
```

The expected results from the test cases are compatible with the original [WordNet::Similarity](http://wn-similarity.sourceforge.net/).

## Initial Work

The original author is [Hideki Shima](http://www.cs.cmu.edu/~hideki/).

## License [![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

This software is released under GNU GPL v3 License. See the [LICENSE](LICENSE) file for details.
