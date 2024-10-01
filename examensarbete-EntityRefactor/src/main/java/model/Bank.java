package model;

import model.chatGPT.Prompt;
import model.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {



    private String uploadedFile;

    private TypeOfWord typeOfWord;

    private List<String> selectedWords;

    private String chatResponse;

    private List<AbstractDomain> firstNames;
    private List<AbstractDomain> lastNames;
    private List<AbstractDomain> companyNames;
    private List<AbstractDomain> SSNs;
    private List<AbstractDomain> phoneNumbers;
    private List<AbstractDomain> amounts;
    private List<AbstractDomain> locations;
    private List<AbstractDomain> emails;
    public Bank() {
        selectedWords = new ArrayList<>();
        firstNames = new ArrayList<>();
        lastNames = new ArrayList<>();
        companyNames = new ArrayList<>();
        phoneNumbers = new ArrayList<>();
        locations = new ArrayList<>();
        SSNs = new ArrayList<>();
        amounts = new ArrayList<>();
        emails = new ArrayList<>();
    }

    public List<String> getSelectedWords() {
        return selectedWords;
    }

    public void addSelectedWords(String word) {
        selectedWords.add(word);
    }


    public void removeSelectedWords(String word) {
        selectedWords.remove(word);
    }


    public AbstractDomain createDomain(TypeOfWord word, String realValue){
        switch (word) {
            case FIRSTNAME:
                AbstractDomain fName = new FirstName(firstNames.size(), realValue);
                firstNames.add(fName);
                return fName;

            case LASTNAME:
                AbstractDomain lName = new LastName(lastNames.size(), realValue);
                lastNames.add(lName);
                return lName;

            case COMPANYNAME:
                AbstractDomain cName = new CompanyName(companyNames.size(), realValue);
                companyNames.add(cName);
                return cName;

            case PHONENUMBER:
                AbstractDomain pNum = new PhoneNumber(phoneNumbers.size(), realValue);
                phoneNumbers.add(pNum);
                return pNum;


            case SSN:
                AbstractDomain ssn = new SSN(SSNs.size(), realValue);
                SSNs.add(ssn);
                return ssn;

            case AMOUNT:
                AbstractDomain amount = new Amount(amounts.size(), realValue);
                amounts.add(amount);
                return amount;
            case LOCATION:
                AbstractDomain location = new Location(locations.size(), realValue);
                locations.add(location);
                return location;
            case EMAIL:
                AbstractDomain email = new Email(emails.size(), realValue);
                emails.add(email);
                return email;

            default:
                return null;
        }

    }

    public List<AbstractDomain> getFirstNames(){
        return firstNames;
    }

    public List<AbstractDomain> getLastNames() {
        return lastNames;
    }

    public List<AbstractDomain> getCompanyNames() {
        return companyNames;
    }

    public List<AbstractDomain> getSSNs() {
        return SSNs;
    }

    public List<AbstractDomain> getPhoneNumbers() {
        return phoneNumbers;
    }

    public List<AbstractDomain> getAmounts() {
        return amounts;
    }

    public List<AbstractDomain> getLocations() {
        return locations;
    }

    public List<AbstractDomain> getEmails() {
        return emails;
    }

    public String deAnonymizeWord(String key) {
        return removeFromList(key).getRealValue();

    }

    public AbstractDomain removeFromList(String key){
        int index = Integer.parseInt(key.substring(key.indexOf(":") + 1 , key.indexOf("]")));
        String type =  key.substring(key.indexOf("[")+ 1 , key.indexOf(":"));
        switch (type) {
            case "firstname":
                AbstractDomain fName =  firstNames.remove(index - 1);
                setUploadedFile(getUploadedFile().replace(key, fName.getRealValue()));
                changeIndex(firstNames);
                setUploadedFile(updateIndex(index, type));
                return fName;
            case "lastname":
                AbstractDomain lName =  lastNames.remove(index - 1);
                setUploadedFile(getUploadedFile().replace(key, lName.getRealValue()));
                changeIndex(lastNames);
                setUploadedFile(updateIndex(index, type));
                return lName;

            case "companyname":
                AbstractDomain cName =  companyNames.remove(index - 1);
                setUploadedFile(getUploadedFile().replace(key, cName.getRealValue()));
                changeIndex(companyNames);
                setUploadedFile(updateIndex(index, type));
                return cName;

            case "ssn":
                AbstractDomain ssn =  SSNs.remove(index - 1);
                setUploadedFile(getUploadedFile().replace(key, ssn.getRealValue()));
                changeIndex(SSNs);
                setUploadedFile(updateIndex(index, type));
                return ssn;

            case "phonenumber":
                AbstractDomain pNum =  phoneNumbers.remove(index - 1);
                setUploadedFile(getUploadedFile().replace(key, pNum.getRealValue()));
                changeIndex(phoneNumbers);
                setUploadedFile(updateIndex(index, type));
                return pNum;

            case "amount":
                AbstractDomain amount =  amounts.remove(index - 1);
                setUploadedFile(getUploadedFile().replace(key, amount.getRealValue()));
                changeIndex(amounts);
                setUploadedFile(updateIndex(index, type));
                return amount;
            case "location":
                AbstractDomain location =  locations.remove(index - 1);
                setUploadedFile(getUploadedFile().replace(key, location.getRealValue()));
                changeIndex(locations);
                setUploadedFile(updateIndex(index, type));
                return location;
            case "email":
                AbstractDomain email =  emails.remove(index - 1);
                setUploadedFile(getUploadedFile().replace(key, email.getRealValue()));
                changeIndex(emails);
                setUploadedFile(updateIndex(index, type));
                return email;
            default:
                return null;
        }

    }

    public void changeIndex(List<AbstractDomain> domains){
        for(int i = 0; i < domains.size(); i++) {
            domains.get(i).setIndex(i);
        }
    }

    public String updateIndex( int index, String type) {

        Pattern pattern = Pattern.compile("\\[" + type + ":(\\d+)]");
        Matcher matcher = pattern.matcher(getUploadedFile());


        StringBuilder builder = new StringBuilder();
        int lastAppendPosition = 0;

        while (matcher.find()) {
            int currentIndex = Integer.parseInt(matcher.group(1));
            if (currentIndex == index) {
                builder.append(getUploadedFile(), lastAppendPosition, matcher.start());
                lastAppendPosition = matcher.end();
            } else if (currentIndex > index) {
                builder.append(getUploadedFile(), lastAppendPosition, matcher.start());
                builder.append("[" + type + ":" + (currentIndex - 1) + "]");
                lastAppendPosition = matcher.end();
            }
        }

        builder.append(getUploadedFile().substring(lastAppendPosition));
        return builder.toString();


    }

    public void setUploadedFile(String text) {
        this.uploadedFile = text;
    }

    public String getUploadedFile() {
        return uploadedFile;
    }


    public String sendPrompt(String question) {
        Prompt prompt = new Prompt();
        prompt.setText(uploadedFile);
        chatResponse = prompt.sendPrompt(question);
        return chatResponse;
    }

    public String getChatResponse(){
        return chatResponse;
    }



}


