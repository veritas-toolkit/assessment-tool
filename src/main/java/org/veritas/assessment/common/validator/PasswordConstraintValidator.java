/*
 * Copyright 2021 MAS Veritas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.veritas.assessment.common.validator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.passay.*;
import org.veritas.assessment.common.annotation.ValidPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    PasswordValidator validator;

    @Override
    @SneakyThrows
    public void initialize(final ValidPassword arg0) {
        //customizing validation messages
        Properties props = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("passay.properties");
        props.load(inputStream);
        MessageResolver resolver = new PropertiesMessageResolver(props);
        validator = new PasswordValidator(resolver, Arrays.asList(
                // length between 8 and 20 characters
                new LengthRule(8, 20),
                new CharacterCharacteristicsRule(
                        2,
                        // at least one upper-case character
                        new CharacterRule(EnglishCharacterData.UpperCase, 1),
                        // at least one lower-case character
//                        new CharacterRule(EnglishCharacterData.LowerCase, 1),
                        // at least one digit character
                        new CharacterRule(EnglishCharacterData.Digit, 1),
                        // at least one symbol (special character)
                        new CharacterRule(EnglishCharacterData.Special, 1)
                ),
                // rejects passwords that contain a sequence of >= 5 characters alphabetical  (e.g. abcdef)
//                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                // rejects passwords that contain a sequence of >= 5 characters qwerty (e.g. qwerty)
//                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
                // rejects passwords that contain a sequence of >= 5 characters numerical   (e.g. 5)
//                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
                // no whitespace
                new WhitespaceRule()
        ));
    }

    @SneakyThrows
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            password = "";
        }
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        } else {
//            List<String> messages = validator.getMessages(result);
//            String messageTemplate = String.join(",", messages);

            final String messageTemplate = "length 8-20; no blank characters; " +
                    "at least two types of numbers, uppercase letters, and special characters.";
            context.buildConstraintViolationWithTemplate(messageTemplate)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }
    }
}
