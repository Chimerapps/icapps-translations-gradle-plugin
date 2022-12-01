# icapps-translations android gradle plugin

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.chimerapps.gradle/icapps-translations-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.chimerapps.gradle/icapps-translations-plugin)

A simple plugin which downloads translations from icapps translation tool and saves the files to the project.

Usage:
```
//Add the following to your root build.gradle:
buildscript {
    repositories {
        mavenCentral()
    }
    
    dependencies {
        classpath 'com.chimerapps.gradle:icapps-translations-plugin:<latest_version>'
    }
}

//Add the following to your project build.gradle file
apply plugin: 'com.chimerapps.gradle.icapps-translations'

//Configure the plugin
icappsTranslations {
    apiKey 'your-api-key-here'              //For legacy translation tool translations
    projectKey 'your-project-key-here'      //For new translation tool translations
    projectToken 'your-project-token-here'  //For new translation tool translations
    fileName 'strings.xml'                  //Default
    defaultLanguage = ""                    //Default, this wil place all the languages in values-{language}, you can select one languge to store the translations in values/strings.xml. This is handy when you only use one translation.
    sourceRoot 'src/main/res'               //Default, will replace occurences of {language} with the (transformed) language
    fileType 'xml'                          //Default, can be xml, json or strings (in new translation tool, probably xml and json in legacy)
    
    languageRename { languageCode ->                //Default is identity transformation (return languageCode)
        return languageCode.replace('_','-r')
    }
    sourceRootProvider { languageCode ->            //Function that provides source root based on language code (this is the raw language code)
        def renamed = languageRename(languageCode)
        return sourceRoot.replace("{language}", (renamed == null) ? code : renamed)
    }
    
    fileNameProvider { languageCode ->      //Function that can generate different file names based on language code (this is the raw language code)
        return fileName
    }
    
    folderProvider { languageCode ->        //Function that provides the folder based on the (raw) language code
        def renamed = languageRename(languageCode)
        return "values-"+(renamed == null) ? code : renamed)
    }
    
    languageFilter { language ->            //Function that filters languages. The language passed is the raw language code
        return true
    }

    keyTransformer { key ->                 //Function that allows you to transform the keys before writing. Currently only works with xml file type. Return null to keep unchanged
        return key                 
    }

    configuration("test") {
        apiKey 'read-only-key2'     //Uses the default fileName and sourceRoot. Not inherited. Specifying lambdas for sub-configurations must use '='
        // Or projectKey and projectToken
    }

    configuration("release") {
        apiKey 'read-only-key3'     //Uses the default fileName and sourceRoot. Not inherited. Specifying lambdas for sub-configurations must use '='
        // Or projectKey and projectToken
    }
}
```

You can then call the tasks from the command line:
```
./gradlew updateicappsTranslations            //Runs all translations tasks
./gradlew updateDefaulticappsTranslations     //Runs the translation task defined by the outer scope
./gradlew updateTesticappsTranslations        //Runs the translation task defined by the 'test' configuration
./gradlew updateReleaseicappsTranslations     //Runs the translation task defined by the 'release' configuration
```

**NOTE:** If the api key or project key/token is missing from the outer scope, the 'default' configuration is not included as task
