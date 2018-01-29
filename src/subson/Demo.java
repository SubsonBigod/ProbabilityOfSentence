package subson;

/**
 * @Author subson.
 * @E-mail subson@qq.com
 */
public class Demo {

    public static void main(String[] args) {

        String sentence = "基于B-gram的句子概率计算";

        // 使用语料库进行训练计算
        ProbabilityOfSentence prob1 = new ProbabilityOfSentence()
                .addCorpus("千万级巨型汉语词库/data")
                .setAddOne(Math.pow(10, -10))
                .goIntoEffect();
        System.out.println(prob1.probability(sentence));
        System.out.println(prob1.probability(sentence, 6));

        // 导入模型文件进行计算
        ProbabilityOfSentence prob2 = new ProbabilityOfSentence()
                .setAddOne(Math.pow(10, -10))
                .goIntoEffect();
        System.out.println(prob2.probability(sentence));
        System.out.println(prob2.probability(sentence, 6));

    }

}
