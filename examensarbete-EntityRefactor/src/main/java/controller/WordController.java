package controller;

import model.Anonymizer;
import model.Bank;
import model.TypeOfWord;
import model.domain.AbstractDomain;

import java.util.List;

public class WordController {
    Bank bank;

    Anonymizer anonymizer;
    public WordController() {
        bank = new Bank();
        anonymizer = new Anonymizer(bank);
    }
    public List<String> getSelectedWords() {
        return bank.getSelectedWords();
    }


    public void addSelectedWords(String word){
        bank.addSelectedWords(word);
    }


    public void removeSelectedWords(String word){
        bank.removeSelectedWords(word);
    }

    public void setUploadedFile(String text) {
        bank.setUploadedFile(text);
    }
    public String getUploadedFile() {
        return bank.getUploadedFile();
    }


    public String sendPrompt(String question) {
        return bank.sendPrompt(question);
    }


//    public String anonymizeWord(String word) {
//        return anonymizer.anonymizeWord(bank.getTypeWord(), word);
//    }

    public String deAnonymizeChatResponse(){
        return anonymizer.deAnonymizeChatResponse();
    }


    public String replaceWord(AbstractDomain domain){
        return anonymizer.replaceWord(domain);
    }

    public String deAnonymizeWord(String key) {
        return anonymizer.deAnonymizeWord(key);
    }

    public AbstractDomain createDomain(TypeOfWord word, String realValue){
        return bank.createDomain(word, realValue);
    }

    public List<AbstractDomain> getFirstName() {
        return bank.getFirstNames();
    }

    public List<AbstractDomain> getLastNames() {
        return bank.getLastNames();
    }

    public List<AbstractDomain> getCompanyNames() {
        return bank.getCompanyNames();
    }

    public List<AbstractDomain> getPhoneNumbers() {
        return bank.getPhoneNumbers();
    }

    public List<AbstractDomain> getSSNs() {
        return bank.getSSNs();
    }

    public List<AbstractDomain> getAmounts() {
        return bank.getAmounts();
    }

    public List<AbstractDomain> getLocations() {
        return bank.getLocations();
    }
    public List<AbstractDomain> getEmails() {
        return bank.getEmails();
    }

}
