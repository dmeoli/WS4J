# WordNet Similarity for Java [![Build Status](https://travis-ci.org/DonatoMeoli/WS4J.svg?branch=master)](https://travis-ci.org/DonatoMeoli/WS4J)

This project was exported from the original [Google Code Location](http://code.google.com/p/ws4j).
The purpose is to publish an artifact to Maven Central, to fix bugs and to use 
[JWI - the MIT Java WordNet Interface](https://projects.csail.mit.edu/jwi/) as default lexical database which 
is the fastest Java library for interfacing to WordNet. 

## Introduction

WordNet Similarity for Java provides a pure Java APIs for several published semantic relatedness/similarity algorithms 
listed below for, in theory, any WordNet instance. 
You can immediately use WS4J on Princeton's English WordNet 3.0 & 
[MIT Java WordNet Interface](https://projects.csail.mit.edu/jwi/) 2.4.0, from your Java program. 
The codebase is mostly a Java re-implementation of [WordNet-Similarity-2.05](http://wn-similarity.sourceforge.net/) 
(written in Perl) using the same data files as seen in src/main/resources, with some test cases for verifying the same 
logic. WS4J designed to be thread safe.

The semantic relatedness/similarity metrics available are:

 - [HSO](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/hso.pm): 
 [Hirst & St-Onge, 1998](https://scholar.google.com/scholar?q=Lexical+chains+as+representations+of+context+for+the+detection+and+correction+of+malapropisms) - 
 Two lexicalized concepts are semantically close if their WordNet synsets are connected by a path that is not too long 
 and that "does not change direction too often";
 - [LCH](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/lch.pm): 
 [Leacock & Chodorow, 1998](https://scholar.google.com/scholar?q=Combining+local+context+and+WordNet+similarity+for+word+sense+identification) - 
 This measure relies on the length of the shortest path between two synsets for their measure of similarity. They limit 
 their attention to IS-A links and scale the path length by the overall depth D of the taxonomy; 
 - [LESK](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/lesk.pm) : 
 [Banerjee & Pedersen, 2002](https://scholar.google.com/scholar?q=An+Adapted+Lesk+Algorithm+for+Word+Sense+Disambiguation+Using+WordNet) - 
 Lesk (1985) proposed that the relatedness of two words is proportional to to the extent of overlaps of their dictionary 
 definitions. Banerjee and Pedersen (2002) extended this notion to use WordNet as the dictionary for the word definitions;
 - [WUP](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/wup.pm): 
 [Wu & Palmer, 1994](https://scholar.google.com/scholar?q=Verb+semantics+and+lexical+selection) - The Wu & Palmer 
 measure calculates relatedness by considering the depths of the two synsets in the WordNet taxonomies, along with the 
 depth of the LCS; 
 - [RES](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/res.pm): 
 [Resnik, 1995](https://scholar.google.com/scholar?q=Using+information+content+to+evaluate+semantic+similarity+in+a+taxonomy) - 
 Resnik defined the similarity between two synsets to be the information content of their lowest super-ordinate (most 
 specific common subsumer);
 - [JCN](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/jcn.pm): 
 [Jiang & Conrath, 1997](https://scholar.google.com/scholar?q=Semantic+similarity+based+on+corpus+statistics+and+lexical+taxonomy) - 
 Also uses the notion of information content, but in the form of the conditional probability of encountering an instance 
 of a child-synsetID given an instance of a parent synsetID: 
 1 / jcn_distance, where jcn_distance is equal to IC(synset1) + IC(synset2) - 2 * IC(lcs);
 - [LIN](http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/lin.pm): 
 [Lin, 1998](https://scholar.google.com/scholar?q=An+information-theoretic+definition+of+similarity) - Math equation is 
 modified a little bit from Jiang and Conrath: 2 * IC(lcs) / (IC(synset1) + IC(synset2)). Where IC(x) is the information 
 content of x. One can observe, then, that the relatedness value will be greater-than or equal-to zero and less-than or 
 equal-to one.

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
these dependencies can be written such as: 

    <dependencies>
      <dependency>
        <groupId>edu.mit.csail</groupId>
        <artifactId>jwi</artifactId>
        <version>2.4.0</version>
        <scope>system</scope> 
        <systemPath>${basedir}/lib/jwi.jar</systemPath>
      </dependency>     
      <dependency>
        <groupId>edu.uniba.di.lacam.kdde</groupId>
        <artifactId>ws4j</artifactId>
        <version>1.0.1</version>
        <scope>system</scope> 
        <systemPath>${basedir}/lib/ws4j.jar</systemPath>
      </dependency>           
    </dependencies>

## Running the tests

To run JUnit test cases:
   
```
$ mvn test
```

The expected results from the test cases are compatible with the original 
[WordNet::Similarity](http://wn-similarity.sourceforge.net/) written in Perl.

## Initial Work

The original author is [Hideki Shima](http://www.cs.cmu.edu/~hideki/).

## License [![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

This software is released under GNU GPL v3 License. See the [LICENSE](LICENSE) file for details.
