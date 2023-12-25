package com.bee.chat.util;

import org.passay.*;

import java.util.ArrayList;
import java.util.List;

public class PasswordUtil {

    private PasswordUtil(){
    }

    private static  final PasswordGenerator passwordGenerator = new PasswordGenerator();

    private static final PasswordValidator validator = new PasswordValidator(
            new LengthRule(8, 16),
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1),
            new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
            new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
            new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
            new WhitespaceRule());

    private static List<CharacterRule> getRules() {
        List<CharacterRule> rules = new ArrayList<>();
        rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        rules.add(new CharacterRule(EnglishCharacterData.Digit, 1));

        CharacterData specialChars = new CharacterData() {
            @Override
            public String getErrorCode() {
                return "ERROR_CODE";
            }

            @Override
            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(1);

        rules.add(splCharRule);

        return rules;
    }

    public static String generatePassword() {
        return passwordGenerator.generatePassword(15, getRules());
    }

    public static boolean validatePassword(String password) {
        return validator.validate(new PasswordData(password)).isValid();
    }
}
