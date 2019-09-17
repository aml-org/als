#!groovy

pipeline {
  agent {
    dockerfile true
  }
  environment {
    NEXUS = credentials('exchange-nexus')
    NEXUSIQ = credentials('nexus-iq')
    ALSP_TOKEN     = credentials('NewALSPToken')
  }
  stages {
    stage('Test') {
      steps {
        wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
          sh 'sbt -mem 4096 -Dsbt.global.base=.sbt -Dsbt.boot.directory=.sbt -Dsbt.ivy.home=.ivy2 clean coverage test coverageReport'
        }
      }
    }
    stage('Coverage') {
      when {
        anyOf {
          branch 'master'
          branch 'devel'
        }
      }
      steps {
        wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
          withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sonarqube-official', passwordVariable: 'SONAR_SERVER_TOKEN', usernameVariable: 'SONAR_SERVER_URL']]) {
            sh 'sbt -Dsonar.host.url=${SONAR_SERVER_URL} sonarScan'
          }
        }
      }
    }
    stage('nexusIq'){
      when {
        anyOf {
          branch 'master'
          branch 'devel'
        }
      }
      steps {
        wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
            sh './gradlew nexusIq'
        }
      }
    }
    stage('Publish') {
      when {
        anyOf {
          branch 'master'
          branch 'devel'
          branch 'rc/*'
          branch 'fat-jar-publish'
          branch 'support/*'
        }
      }
      steps {
        sh 'sbt publish'
      }
    }
    stage('Trigger Dependencies'){
        when {
            anyOf{
                branch 'devel'
            }
        }
        steps {
            sh 'curl https://jenkins-onprem.build.msap.io/generic-webhook-trigger/invoke?token=$ALSP_TOKEN'
        }
    }
  }
}
