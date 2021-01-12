#!groovy
def slackChannel = '#als-bot'
def failedStage = ""
def color = '#FF8C00'
def headerFlavour = "WARNING"
pipeline {
    agent {
        dockerfile { 
            label 'gn-8-16-1'
        }
    }
    environment {
        NEXUS = credentials('exchange-nexus')
        NEXUSIQ = credentials('nexus-iq')
        ALSP_TOKEN = credentials('NewALSPToken')
        NPM_TOKEN = credentials('npm-mulesoft')
        NPM_CONFIG_PRODUCTION = true
        NODE_ENV = 'dev'
        VERSION = "${env.BUILD_NUMBER}"
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
                    branch 'develop'
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
                    branch 'develop'
                    branch 'nexus-iq/*'
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
        stage('Publish node client') {
            when {
                anyOf {
                    branch 'master'
                    branch 'develop'
                    branch 'rc/*'
                }
            }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    script {
                        sh 'sbt -mem 4096 -Dsbt.global.base=.sbt -Dsbt.boot.directory=.sbt -Dsbt.ivy.home=.ivy2 buildNodeJsClient'
                        def statusCode = 1
                        dir("als-node-client/node-package") {
                            echo "Publishing NPM package build: ${VERSION}."
                            statusCode = sh script:"scripts/publish.sh ${VERSION} ${NPM_TOKEN} ${env.BRANCH_NAME}", returnStatus:true
                        }
                        if(statusCode != 0) {
                            failedStage = failedStage + " PUBLISH-NODE-JS "
                            unstable "Failed Node client publication"
                        }
                    }

                }
            }
        }
        stage('Publish als-server JS') {
            when {
                anyOf {
                    branch 'master'
                    branch 'develop'
                    branch 'rc/*'
                }
            }
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    script {
                        sh 'sbt -mem 4096 -Dsbt.global.base=.sbt -Dsbt.boot.directory=.sbt -Dsbt.ivy.home=.ivy2 buildJsServerLibrary'
                        def statusCode = 1
                        dir("als-server/js/node-package") {
                            echo "Publishing NPM package build: ${VERSION}."
                            statusCode = sh script:"scripts/publish.sh ${VERSION} ${NPM_TOKEN} ${env.BRANCH_NAME}", returnStatus:true
                        }
                        if(statusCode != 0) {
                            failedStage = failedStage + " PUBLISH-SERVER-JS "
                            unstable "Failed als-server JS publication"
                        }
                    }

                }
            }
        }
        stage('Publish') {
            when {
                anyOf {
                    branch 'master'
                    branch 'develop'
                    branch 'rc/*'
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
        stage("Report to Slack") {
            when {
                anyOf {
                    branch 'master'
                    branch 'support/*'
                    branch 'develop'
                }
            }
            steps {
                script {
                    if (!failedStage.isEmpty()) {
                        if (env.BRANCH_NAME == 'master') {
                            color = '#FF0000'
                            headerFlavour = "RED ALERT"
                        } else if (env.BRANCH_NAME == 'develop') {
                            color = '#FFD700'
                        }
                        slackSend color: color, channel: "${slackChannel}", message: ":alert: ${headerFlavour}! :alert: Build failed!. \n\tBranch: ${env.BRANCH_NAME}\n\tStage:${failedStage}\n(See ${env.BUILD_URL})\n"
                        currentBuild.status = "FAILURE"
                    } else if (env.BRANCH_NAME == 'master') {
                        slackSend color: '#00FF00', channel: "${slackChannel}", message: ":ok_hand: Master Publish OK! :ok_hand:"
                    } else if (currentBuild.getPreviousBuild().result != null && currentBuild.getPreviousBuild().result.toString() != 'SUCCESS') {
                        slackSend color: '#00FF00', channel: "${slackChannel}", message: ":ok_hand: Back to Green!! :ok_hand:\n\tBranch: ${env.BRANCH_NAME}\n(See ${env.BUILD_URL})\n"
                    }
                }
            }
        }
    }
}
