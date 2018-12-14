#!groovy

pipeline {
  agent {
    dockerfile true
  }
  environment {
    NEXUS = credentials('exchange-nexus')
    NEXUSIQ = credentials('nexus-iq')
  }
  stages {
    stage('Test') {
      steps {
        wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
          withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sonarqube-official', passwordVariable: 'SONAR_SERVER_TOKEN', usernameVariable: 'SONAR_SERVER_URL']]) {
            sh 'sbt -mem 4096 -Dsbt.global.base=.sbt -Dsbt.boot.directory=.sbt -Dsbt.ivy.home=.ivy2 clean coverage test coverageReport runSonar'
          }
        }
      }
    }
    stage('nexusIq'){
      when {
        anyOf {
          branch 'master'
          branch 'release/*'
        }
      }
      steps {
        wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
            sh './gradlew nexusIq'
        }
      }
    }
    stage('Publish') {
      steps {
        sh 'sbt -Dsbt.global.base=.sbt -Dsbt.boot.directory=.sbt -Dsbt.ivy.home=.ivy2 publish'
      }
    }
  }
}
