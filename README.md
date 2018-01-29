# 关于*ProbabilityOfSentence*（句子概率计算）的说明

## 概述

该“句子相似度计算”基于B-gram算法，若不了解请自行百度。

如果急于使用，请下载笔者已经训练好的模型文件（model.txt，置于项目根目录）；如果想自行训练，可以使用语料库（千万级巨型汉语词库）；下面是下载地址：

链接：[https://pan.baidu.com/s/1c3WWoxi](https://pan.baidu.com/s/1c3WWoxi) 密码：uypd

由于笔者所使用的语料库是基于词语的，所以对于词语的计算效果更佳，但是常规句子的计算可能并不理想，建议自行寻找语料库进行训练。

## 说明

输入为文本文件（一句一行或者一段一行），支持批量语料输入，可以多次或者单次调用addCorpus方法进行设置。

训练好之后会自行保存模型文件（命名为“model.txt”）到项目根目录。如果不配置输入语料且根目录保存有模型文件，则会直接导入模型文件。

B-gram采用的平滑处理手段是“add one smoothing”，但是并非真的加一，可以加一个大于0小于1的数，该参数使用setAddOne方法进行设置。

最后使用goIntoEffect方法让所有设置生效。

使用probability方法计算句子的概率，该方法可选的第二个参数len表示进行平均计算的字符长度，即每len个连续字符计算一次，之后取平均。

```java
// 使用语料库进行训练计算
ProbabilityOfSentence prob = new ProbabilityOfSentence()
        .addCorpus("千万级巨型汉语词库/data")
        .setAddOne(Math.pow(10, -10))
        .goIntoEffect();
System.out.println(prob.probability(sentence));
System.out.println(prob.probability(sentence, 6));
```

```java
// 导入模型文件进行计算
ProbabilityOfSentence prob = new ProbabilityOfSentence()
        .goIntoEffect();
System.out.println(prob.probability(sentence));
System.out.println(prob.probability(sentence, 6));
```