#!groovy
def slackChannel = '#als-bot'
def failedStage = ""
def color = '#FF8C00'
def headerFlavour = "WARNING"

pipeline {
    agent {
        dockerfile true
    }
    environment {
        NEXUS = credentials('exchange-nexus')
        NEXUSIQ = credentials('nexus-iq')
        ALSP_TOKEN = credentials('NewALSPToken')
    }
    stages {
        stage('Clean') {
            steps {
                sh "git clean -fdx"
            }
        }
        stage('Test') {
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    script {
                        try {
                            sh 'sbt -mem 4096 -Dsbt.global.base=.sbt -Dsbt.boot.directory=.sbt -Dsbt.ivy.home=.ivy2 clean coverage test coverageReport'
                        } catch (e) {
                            failedStage = failedStage + " TEST "
                            unstable "Failed tests"
                        }
                    }
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
                        script {
                            try {
                                if (failedStage.isEmpty()) {
                                    sh 'sbt -Dsonar.host.url=${SONAR_SERVER_URL} sonarScan'
                                }
                            } catch (e) {
                                failedStage = failedStage + " COVERAGE "
                                unstable "Failed coverage"
                            }
                        }
                    }
                }
            }
        }
        stage('nexusIq') {
            when {
                anyOf {
                    branch 'master'
                    branch 'devel'
                }
            }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    script {
                        try {
                            if (failedStage.isEmpty()) {
                                sh './gradlew nexusIq'
                            }
                        } catch (e) {
                            failedStage = failedStage + " NEXUSIQ "
                            unstable "Failed Nexus IQ"
                        }
                    }
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
                }
            }
            steps {
                script {
                    try {
                        if (failedStage.isEmpty()) {
                            sh 'sbt publish'
                        }
                    } catch (e) {
                        failedStage = failedStage + " PUBLISH "
                        unstable "Failed publication"
                    }
                }
            }
        }
        stage('Trigger Dependencies') {
            when {
                anyOf {
                    branch 'devel'
                }
            }
            steps {
                script {
                    try {
                        if (failedStage.isEmpty()) {
                            sh 'curl https://jenkins-onprem.build.msap.io/generic-webhook-trigger/invoke?token=$ALSP_TOKEN'
                            build job: 'ALS/als-client/master', wait: false
                        }
                    } catch (e) {
                        failedStage = failedStage + " DEPENDENCIES "
                        unstable "Failed dependencies"
                    }
                }
            }
        }
        stage("Report to Slack") {
            steps {
                script {
                    if (!failedStage.isEmpty()) {
                        if (env.BRANCH_NAME == 'master') {
                            color = '#FF0000'
                            headerFlavour = "RED ALERT"
                        } else if (env.BRANCH_NAME == 'devel') {
                            color = '#FFD700'
                        }
                        slackSend color: color, channel: "${slackChannel}", message: ":alert: ${headerFlavour}! :alert: Build failed!. \n\tBranch: ${env.BRANCH_NAME}\n\tStage:${failedStage}\n(See ${env.BUILD_URL})\n"
                        currentBuild.status = "FAILURE"
                    } else if (env.BRANCH_NAME == 'master') {
                        slackSend color: '#00FF00', channel: "${slackChannel}", message: ":ok_hand: Master Publish OK! :ok_hand:"
                    }
                }
            }
        }
    }
}
