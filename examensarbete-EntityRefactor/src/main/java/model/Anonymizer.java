package model;

import model.domain.AbstractDomain;
import model.domain.FirstName;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Anonymizer {

    private Bank bank;

    public Anonymizer(Bank bank) {
        this.bank = bank;
    }

//    private String anonymize(TypeOfWord typeOfWord, String word) {
//        return "[" + typeOfWord.toString().toLowerCase() +
//                ":" + bank.count(typeOfWord) + "]";
//    }

//    public String anonymizeWord(TypeOfWord typeOfWord, String word) {
//        String anonmyizedWord = anonymize(typeOfWord, word);
//        bank.addSelectedWords(anonmyizedWord);
//        bank.addAnonymizedWord(anonmyizedWord, word);
//        replaceWord(anonmyizedWord);
//        return anonmyizedWord;
//    }

    public String replaceWord(AbstractDomain domain) {
        String file = bank.getUploadedFile();
        // Adjusting the regex pattern to cover edge cases with single occurrences and punctuation
        String pattern = "(?i)\\b" + Pattern.quote(domain.getRealValue()) + "\\b(?=[.!?\\-,;]*($|\\s))";
        String replacement = domain.toString(); // Simple replacement
        bank.setUploadedFile(file.replaceAll(pattern, replacement));
        return bank.getUploadedFile();
    }




    public String deAnonymizeWord(String key) {
        return bank.deAnonymizeWord(key);
    }


    public String deAnonymizeChatResponse() {
        String chatResp = bank.getChatResponse();

        System.out.println("Before: " + chatResp);
        List<List<AbstractDomain>> allDomains = Arrays.asList(
                bank.getFirstNames(),
                bank.getLastNames(),
                bank.getSSNs(),
                bank.getPhoneNumbers(),
                bank.getAmounts(),
                bank.getCompanyNames(),
                bank.getLocations(),
                bank.getEmails()
        );

        for (List<AbstractDomain> domainList : allDomains) {
            // Iterate over each domain in the current domain list
            for (AbstractDomain domain : domainList) {
                chatResp = chatResp.replaceAll(Pattern.quote(domain.toString()), domain.getRealValue());
            }
        }
        System.out.println("After: "  + chatResp);
        return chatResp;
    }

}
