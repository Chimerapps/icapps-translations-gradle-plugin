# icapps-translations android gradle plugin

[ ![Download](https://api.bintray.com/packages/nicolaverbeeck/maven/icapps-translations-gradle-plugin/images/download.svg) ](https://bintray.com/nicolaverbeeck/maven/icapps-translations-gradle-plugin/_latestVersion)

A simple plugin which downloads translations from icapps translation tool and saves the files to the project.

Usage:
```
//Add the following to your root build.gradle:
buildscript {
    repositories {
        jcenter()
    }
    
    dependencies {
        classpath 'com.chimerapps.gradle:icapps-translations-plugin:<latest_version>'
    }
}

//Add the following to your project build.gradle file
apply plugin: 'com.chimerapps.gradle.icapps-translations'

//Configure the plugin
icappsTranslations {
    apiKey 'your-api-key-here'
    fileName 'strings.xml'       //Default
    sourceRoot 'src/main/res'    //Default, will replace occurences of {language} with the (transformed) language
    
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

    configuration("test") {
        apiKey 'read-only-key2'     //Uses the default fileName and sourceRoot. Not inherited. Specifying lambdas for sub-configurations must use '='
    }

    configuration("release") {
        apiKey 'read-only-key3'     //Uses the default fileName and sourceRoot. Not inherited. Specifying lambdas for sub-configurations must use '='
    }
}
```

You can then call the tasks from the command line:
```
./gradlew updateiCappsTranslations            //Runs all translations tasks
./gradlew updateDefaultiCAppsTranslations     //Runs the translation task defined by the outer scope
./gradlew updateTestiCappsTranslations        //Runs the translation task defined by the 'test' configuration
./gradlew updateReleaseiCappsTranslations     //Runs the translation task defined by the 'release' configuration
```

**NOTE:** If the api key is missing from the outer scope, the 'default' configuration is not included as task
