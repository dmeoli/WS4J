package edu.uniba.di.lacam.kdde.ws4j.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PorterStemmer {

    static class StringWrapper {

        public String word;

        StringWrapper(String word) {
            this.word = word;
        }
    }

    // This is a consonant. Not "aiueo" and "y" only if preceded by a vowel
    private final static String c = "(?:[^aiueoy]|(?:(?<=[aiueo])y)|\by)";

    // This is a vowel. "aiueo" and "y" if preceded by a consonant
    private final static String v = "(?:[aiueo]|(?:(?<![aiueo])y))";
    private final static Pattern pV = Pattern.compile(v);
    private int extra = 0;

    // The re "/^(?:$c+)?(?:$v+$c+){m}(?:$v+)?$/" is [C](VC)**m[V] in regex
    // Matches if (m > 0)
    private final static Pattern m_gt_0 = Pattern.compile("^(?:"+c+"+)?(?:"+v+"+"+c+"+){1,}(?:"+v+"+)?$");
    // Matches if (m > 1)
    private final static Pattern m_gt_1 = Pattern.compile("^(?:"+c+"+)?(?:"+v+"+"+c+"+){2,}(?:"+v+"+)?$");
    // Matches if (m = 1)
    private final static Pattern m_eq_1 = Pattern.compile("^(?:"+c+"+)?(?:"+v+"+"+c+"+){1}(?:"+v+"+)?$");

    // Matches *o
    private final static Pattern pO = Pattern.compile(c+v+"(?:[^aiueowxy])$");

    // Matches *d
    private final static Pattern pD = Pattern.compile("("+c+")\\1$");

    String stemWord(String word) {
        StringWrapper w = new StringWrapper(word);
        if (w.word.length() > 2) {
            w = step1(w);
            w = step2(w);
            w = step3(w);
            w = step4(w);
            w = step5(w);
        }
        return w.word;
    }

    private final static Pattern p_sses = Pattern.compile("(.+)sses$");
    private final static Pattern p_ies = Pattern.compile("(.+)ies$");
    private final static Pattern p_s = Pattern.compile("(.+[^s])s$");
    private final static Pattern p_eed = Pattern.compile("(.+)eed$");
    private final static Pattern p_ed = Pattern.compile("(.+)ed$");
    private final static Pattern p_ing = Pattern.compile("(.+)ing$");
    private final static Pattern p_at = Pattern.compile("(.+)at$");
    private final static Pattern p_bl = Pattern.compile("(.+)bl$");
    private final static Pattern p_iz = Pattern.compile("(.+)iz$");
    private final static Pattern p_pD = pD;
    private final static Pattern p_lsz = Pattern.compile("[lsz]$");
    private final static Pattern p1b_9 = m_eq_1;
    private final static Pattern p1b_10 = pO;
    private final static Pattern p_y = Pattern.compile("(.+)y$");

    private StringWrapper step1(StringWrapper w) {
        Matcher m1a_1 = p_sses.matcher(w.word);
        Matcher m1a_2 = p_ies.matcher(w.word);
        Matcher m1a_3 = p_s.matcher(w.word);

        if (m1a_1.find()) {
            w.word = m1a_1.group(1) + "ss";
        } else if (m1a_2.find()) {
            w.word = m1a_2.group(1) + "i";
        } else if (m1a_3.find()) {
            w.word = m1a_3.group(1);
        }

        Matcher m_eed = p_eed.matcher(w.word);
        Matcher m_ed = p_ed.matcher(w.word);
        Matcher m_ing = p_ing.matcher(w.word);

        if (m_eed.find()) {
            if (m_gt_0.matcher(m_eed.group(1)).find()) {
                w.word = m_eed.group(1)+"ee";
            }
        } else if (m_ed.find()) {
            if (pV.matcher(m_ed.group(1)).find()) {
                w.word = m_ed.group(1);
                extra = 1;
            }
        } else if (m_ing.find()) {
            if (pV.matcher(m_ing.group(1)).find()) {
                w.word = m_ing.group(1);
                extra = 1;
            }
        }

        Matcher m_ate = p_at.matcher(w.word);
        Matcher m_bl = p_bl.matcher(w.word);
        Matcher m_iz = p_iz.matcher(w.word);
        Matcher m_pD = p_pD.matcher(w.word);
        Matcher m_lsz = p_lsz.matcher(w.word);
        Matcher m1b_9 = p1b_9.matcher(w.word);
        Matcher m1b_10 = p1b_10.matcher(w.word);

        if (extra > 0) {
            if (m_ate.find()) {
                w.word = m_ate.group(1) + "ate";
            } else if (m_bl.find()) {
                w.word = m_bl.group(1) + "ble";
            } else if (m_iz.find()) {
                w.word = m_iz.group(1) + "ize";
            } else if (m_pD.find() && !m_lsz.find()) {
                w.word = w.word.substring(0, w.word.length()-1);
            } else if (m1b_9.find() && m1b_10.find()) {
                w.word += "e";
            }
        }

        sub(w, p_y, pV, "i");
        return w;
    }

    private final static Pattern p_ational = Pattern.compile("(.+)ational$");
    private final static Pattern p_tional = Pattern.compile("(.+)tional$");
    private final static Pattern p_enci = Pattern.compile("(.+)enci$");
    private final static Pattern p_anci = Pattern.compile("(.+)anci$");
    private final static Pattern p_izer = Pattern.compile("(.+)izer$");
    private final static Pattern p_logi = Pattern.compile("(.+)logi$");
    private final static Pattern p_bli = Pattern.compile("(.+)bli$");
    private final static Pattern p_alli = Pattern.compile("(.+)alli$");
    private final static Pattern p_entli = Pattern.compile("(.+)entli$");
    private final static Pattern p_eli = Pattern.compile("(.+)eli$");
    private final static Pattern p_ousli = Pattern.compile("(.+)ousli$");
    private final static Pattern p_ization = Pattern.compile("(.+)ization$");
    private final static Pattern p_ation = Pattern.compile("(.+)ation$");
    private final static Pattern p_ator = Pattern.compile("(.+)ator$");
    private final static Pattern p_alism = Pattern.compile("(.+)alism$");
    private final static Pattern p_iveness = Pattern.compile("(.+)iveness$");
    private final static Pattern p_fulness = Pattern.compile("(.+)fulness$");
    private final static Pattern p_ousness = Pattern.compile("(.+)ousness$");
    private final static Pattern p_aliti = Pattern.compile("(.+)aliti$");
    private final static Pattern p_iviti = Pattern.compile("(.+)iviti$");
    private final static Pattern p_biliti = Pattern.compile("(.+)biliti$");

    private StringWrapper step2(StringWrapper w) {
        String letter = w.word.substring(w.word.length()-2, w.word.length()-1);
        if (letter.equals("a")) {
            if (sub(w, p_ational, m_gt_0, "ate")) {} else
            if (sub(w, p_tional, m_gt_0, "tion")) {}
        } else if (letter.equals("c")) {
            if (sub(w, p_enci, m_gt_0, "ence")) {} else
            if (sub(w, p_anci, m_gt_0, "ance")) {}
        } else if (letter.equals("e")) {
            sub(w, p_izer, m_gt_0, "ize");
        } else if (letter.equals("g")) {
            sub(w, p_logi, m_gt_0, "log");
        } else if (letter.equals("l")) {
            if (sub(w, p_bli, m_gt_0, "ble")) {} else
            if (sub(w, p_alli, m_gt_0, "al")) {} else
            if (sub(w, p_entli, m_gt_0, "ent")) {} else
            if (sub(w, p_eli, m_gt_0, "e")) {} else
            if (sub(w, p_ousli, m_gt_0, "ous")) {}
        } else if (letter.equals("o")) {
            if (sub(w, p_ization, m_gt_0, "ize")) {} else
            if (sub(w, p_ation, m_gt_0, "ate")) {} else
            if (sub(w, p_ator, m_gt_0, "ate")) {}
        } else if (letter.equals("s")) {
            if (sub(w, p_alism, m_gt_0, "al")) {} else
            if (sub(w, p_iveness, m_gt_0, "ive")) {} else
            if (sub(w, p_fulness, m_gt_0, "ful")) {} else
            if (sub(w, p_ousness, m_gt_0, "ous")) {}
        } else if (letter.equals("t")) {
            if (sub(w, p_aliti, m_gt_0, "al")) {} else
            if (sub(w, p_iviti, m_gt_0, "ive")) {} else
            if (sub(w, p_biliti, m_gt_0, "ble")) {}
        }
        return w;
    }

    private final static Pattern p_icate = Pattern.compile("(.+)icate$");
    private final static Pattern p_ative = Pattern.compile("(.+)ative$");
    private final static Pattern p_alize = Pattern.compile("(.+)alize$");
    private final static Pattern p_iciti = Pattern.compile("(.+)iciti$");
    private final static Pattern p_ical = Pattern.compile("(.+)ical$");
    private final static Pattern p_ful = Pattern.compile("(.+)ful$");
    private final static Pattern p_ness = Pattern.compile("(.+)ness$");

    private StringWrapper step3(StringWrapper w) {
        String letter = w.word.substring(w.word.length()-1);
        if (letter.equals("e")) {
            if (sub(w, p_icate, m_gt_0, "IC")) {} else
            if (sub(w, p_ative, m_gt_0, "")) {} else
            if (sub(w, p_alize, m_gt_0, "al")) {}
        } else if (letter.equals("i")) {
            sub(w, p_iciti, m_gt_0, "IC");
        } else if (letter.equals("l")) {
            if (sub(w, p_ical, m_gt_0, "IC")) {} else
            if (sub(w, p_ful, m_gt_0, "")) {}
        } else if (letter.equals("s")) {
            sub(w, p_ness, m_gt_0, "");
        }
        return w;
    }

    private final static Pattern p_al = Pattern.compile("(.+)al$");
    private final static Pattern p_ance = Pattern.compile("(.+)ance$");
    private final static Pattern p_ence = Pattern.compile("(.+)ence$");
    private final static Pattern p_er = Pattern.compile("(.+)er$");
    private final static Pattern p_ic = Pattern.compile("(.+)ic$");
    private final static Pattern p_able = Pattern.compile("(.+)able$");
    private final static Pattern p_ible = Pattern.compile("(.+)ible$");
    private final static Pattern p_ant = Pattern.compile("(.+)ant$");
    private final static Pattern p_ement = Pattern.compile("(.+)ement$");
    private final static Pattern p_ment = Pattern.compile("(.+)ment$");
    private final static Pattern p_ent = Pattern.compile("(.+)ent$");
    private final static Pattern p_ion = Pattern.compile("(.+)ion$");
    private final static Pattern p_st = Pattern.compile("[st]$");
    private final static Pattern p_ou = Pattern.compile("(.+)ou$");
    private final static Pattern p_ism = Pattern.compile("(.+)ism$");
    private final static Pattern p_ate = Pattern.compile("(.+)ate$");
    private final static Pattern p_iti = Pattern.compile("(.+)iti$");
    private final static Pattern p_ous = Pattern.compile("(.+)ous$");
    private final static Pattern p_ive = Pattern.compile("(.+)ive$");
    private final static Pattern p_ize = Pattern.compile("(.+)ize$");

    private StringWrapper step4(StringWrapper w) {

        String letter = w.word.substring(w.word.length()-2, w.word.length()-1);
        if (letter.equals("a")) sub(w, p_al, m_gt_1, "");
        else if (letter.equals("c")) {
            if (sub(w, p_ance, m_gt_1, "")) {} else
            if (sub(w, p_ence, m_gt_1, "")) {}
        } else if (letter.equals("e")) sub(w, p_er, m_gt_1, "");
        else if (letter.equals("i")) sub(w, p_ic, m_gt_1, "");
        else if (letter.equals("l")) {
            if (sub(w, p_able, m_gt_1, "")) {} else
            if (sub(w, p_ible, m_gt_1, "")) {}
        } else if (letter.equals("n")) {
            if (sub(w, p_ant, m_gt_1, "")) {} else
            if (sub(w, p_ement, m_gt_1, "")) {} else
            if (sub(w, p_ment, m_gt_1, "")) {} else
            if (sub(w, p_ent, m_gt_1, "")) {}
        } else if (letter.equals("o")) {
            Matcher m_ion = p_ion.matcher(w.word);
            if (m_ion.find()) {
                if (p_st.matcher(m_ion.group(1)).find() && m_gt_1.matcher(m_ion.group(1)).find()) w.word = m_ion.group(1);
            } else if (sub(w, p_ou, m_gt_1, "")) {}
        } else if (letter.equals("s")) sub(w, p_ism, m_gt_1, "");
        else if (letter.equals("t")) {
            if (sub(w, p_ate, m_gt_1, "")) {} else
            if (sub(w, p_iti, m_gt_1, "")) {}
        } else if (letter.equals("u")) sub(w, p_ous, m_gt_1, "");
        else if (letter.equals("v")) sub(w, p_ive, m_gt_1, "");
        else if (letter.equals("z")) sub(w, p_ize, m_gt_1, "");

        return w;
    }

    private final static Pattern p5_1 = Pattern.compile("(.+)e$");
    private final static Pattern p5_2 = Pattern.compile("ll$");

    private StringWrapper step5(StringWrapper w) {
        Matcher m5_1 = p5_1.matcher(w.word);
        if (m5_1.find()) {
            if (m_gt_1.matcher(m5_1.group(1)).find() ||
                    (m_eq_1.matcher(m5_1.group(1)).find() && pO.matcher(m5_1.group(1)).find())) {
                w.word = m5_1.group(1);
            }
        }

        Matcher m5_2 = p5_2.matcher(w.word);
        if (m5_2.find()) {
            if (m_gt_1.matcher(w.word).find()) {
                w.word = w.word.substring(0, w.word.length()-1);
            }
        }

        return w;
    }

    private boolean sub(StringWrapper w, Pattern p1, Pattern p2, String postfix) {
        Matcher m = p1.matcher(w.word);
        boolean matched = m.find();
        if (matched) {
            if (p2.matcher(m.group(1)).find()) {
                w.word = m.group(1)+postfix;
            }
        }
        return matched;
    }

    private String[] stemWords(String[] words) {
        String[] stemmedWords = new String[words.length];
        for (int i=0; i<words.length; i++) {
            stemmedWords[i] = stemWord(words[i]);
        }
        return stemmedWords;
    }

    public String stemSentence(String sentence) {
        String[] words = stemWords(sentence.split("\\s+"));
        return CollectionUtil.join(" ", Arrays.asList(words));
    }
}