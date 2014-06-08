package com.vladinooo.wordtracker.model;


public class Word {

    private String wordEn;
    private String wordRu;
    private String wordMeaning;

    
    public Word() {
    }


    public Word(String wordEn, String wordRu) {
        this.wordEn = wordEn;
        this.wordRu = wordRu;
    }


	public String getWordEn() {
		return wordEn;
	}


	public void setWordEn(String wordEn) {
		this.wordEn = wordEn;
	}


	public String getWordRu() {
		return wordRu;
	}


	public void setWordRu(String wordRu) {
		this.wordRu = wordRu;
	}


	public String getWordMeaning() {
		return wordMeaning;
	}


	public void setWordMeaning(String wordMeaning) {
		this.wordMeaning = wordMeaning;
	}

    
}