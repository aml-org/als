#!groovy
def slackChannel = '#als-bot'
def failedStage = ""
def color = '#FF8C00'
def headerFlavour = "WARNING"
def publish_version
def projectName = "ALS"
def BRANCH_SUPPORT = "support"

node {
    // Login to dockerhub to prevent rate-limiting. See https://salesforce.quip.com/aqcaAObOcXpF
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'dockerhub-pro-credentials', passwordVariable: 'DOCKERHUB_PASS', usernameVariable: 'DOCKERHUB_USER']]) {
        withEnv(["HOME=${WORKSPACE}", "DOCKER_EMAIL=engineeringservices@mulesoft.com"]) {
            sh(script:"echo '${env.DOCKERHUB_PASS}' | docker login -u '${env.DOCKERHUB_USER}' --password-stdin ", label:"log in to dockerhub")
        }
    }
}

pipeline {
    agent {
        dockerfile {
            label 'gn-8-16-1'
            registryCredentialsId 'dockerhub-pro-credentials'
        }
    }
    parameters {
        string(name: 'AMF_VERSION', defaultValue: '', description: 'AMF version')
        string(name: 'VALIDATOR_VERSION', defaultValue: '', description: 'AMF Custom Validator version')
    }
    environment {
        AMF_VERSION = "$AMF_VERSION"
        VALIDATOR_VERSION = "$VALIDATOR_VERSION"
        NEXUS = credentials('exchange-nexus')
        NEXUSIQ = credentials('nexus-iq')
        NPM = credentials('aml-org-bot')
        NPM_TOKEN = credentials('aml-org-bot-npm-token')
        NPM_CONFIG_PRODUCTION = false
        NODE_ENV = 'dev'
        BUILD_NUMBER = "${env.BUILD_NUMBER}"
        currentVersion = sh(script:"cat dependencies.properties | grep \"version\" | cut -d '=' -f 2", returnStdout: true)
    }
    stages {
        stage('Clean') {
            steps {
                sh "git clean -fdx"
                script {
                    publish_version = "${currentVersion}".replace("\n", "")
                    echo "$publish_version"
                    npmLogin()
                }
            }
        }
        stage('Prepare version') {
            when {
                not {
                    anyOf {
                        branch 'master'
                        branch 'rc/*'
                    }
                }
            }
            steps {
                script {
                    publish_version = "${currentVersion}.${BUILD_NUMBER}".replace("\n", "")
                    echo "$publish_version"
                }
            }
        }
        stage('Prepare Dependency versions') {
            when {
                expression { env.AMF_VERSION?.trim() && env.VALIDATOR_VERSION?.trim() }
            }
            steps {
                script {
                    def statusCode = 0
                    statusCode = sh script:"scripts/dependency-versions.sh", returnStatus:true
                    if(statusCode != 0) {
                        failedStage = failedStage + " Prepare Dependency versions "
                        unstable "Failed to Prepare Dependency versions"
                    }
                }
            }
        }
        stage('Test') {
            steps {
                wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
                    script {
                        try {
                            sh 'sbt -mem 12000 -Dflaky.ignore=true -Dsbt.global.base=.sbt -Dsbt.boot.directory=.sbt -Dsbt.ivy.home=.ivy2 -Djava.io.tmpdir=$HOME clean coverage test coverageAggregate --warn'
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
                    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sf-sonarqube-official', passwordVariable: 'SONAR_SERVER_TOKEN', usernameVariable: 'SONAR_SERVER_URL']]) {
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
                            sh 'sbt --mem 10000 publish -Djava.io.tmpdir=$HOME'
                        }
                    } catch (e) {
                        failedStage = failedStage + " PUBLISH "
                        unstable "Failed publication"
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
                        if (failedStage.isEmpty()) {
                            publish_version_node_client = "${publish_version}".replace("\n", "")
                            echo "$publish_version_node_client"
                            sh 'sbt -mem 10000 -Dsbt.global.base=.sbt -Dsbt.boot.directory=.sbt -Dsbt.ivy.home=.ivy2 buildNodeJsClient -Djava.io.tmpdir=$HOME'
                            def statusCode = 1
                            dir("als-node-client/node-package") {
                                echo "Publishing NPM package: ${publish_version_node_client}"
                                statusCode = sh script:"scripts/publish.sh ${publish_version_node_client} ${env.BRANCH_NAME}", returnStatus:true
                            }
                            if(statusCode != 0) {
                                failedStage = failedStage + " PUBLISH-NODE-JS "
                                unstable "Failed Node client publication"
                            }
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
                        if (failedStage.isEmpty()) {
                            def statusCode = 1
                            statusCode = sh script:'sbt -mem 12000 -Dsbt.global.base=.sbt -Dsbt.boot.directory=.sbt -Dsbt.ivy.home=.ivy2 -Djava.io.tmpdir=$HOME buildJsServerLibrary', returnStatus: true
                            if(statusCode != 0) {
                                failedStage = failedStage + " PUBLISH-SERVER-JS "
                                unstable "Failed als-server JS publication"
                            }
                            dir("als-server/js/node-package") {
                                echo "Publishing NPM package build: ${publish_version}."
                                statusCode = sh script:"scripts/publish.sh ${publish_version} ${env.BRANCH_NAME}", returnStatus:true
                            }
                            if(statusCode != 0) {
                                failedStage = failedStage + " PUBLISH-SERVER-JS "
                                unstable "Failed als-server JS publication"
                            }
                        }
                    }
                }
            }
        }
        stage('Trigger Dependencies') {
            when {
                anyOf {
                    branch 'develop'
                    branch 'rc/*'
                    branch 'master'
                }
            }
            steps {
                script {
                    if (failedStage.isEmpty()) {
                        try {
                            def javaVersion = "${currentVersion}".replace("\n", "")
                            echo "Trigger anypoint-als ($javaVersion) and als-extension ($publish_version)"
                            build job: "ALS/anypoint-als/develop", parameters: [string(name: 'ALS_VERSION', value: "$javaVersion")], wait: false
                        } catch (e) {
                            echo e.getMessage()
                            failedStage = failedStage + " DEPENDENCIES "
                            unstable "Failed dependencies"
                        }
                    }
                }
            }
        }
        stage("Report to Slack") {
            when {
                anyOf {
                    branch 'master'
                    branch BRANCH_SUPPORT + '/*'
                    branch 'rc/*'
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
                        slackSend color: color, channel: "${slackChannel}", message: ":alert: ${headerFlavour}! :alert: ${projectName} Build failed!. \n\tBranch: ${env.BRANCH_NAME}\n\tStage:${failedStage}\n(See ${env.BUILD_URL})\n"
                        currentBuild.status = "FAILURE"
                    } else if (env.BRANCH_NAME.startsWith("rc/")) {
                        slackSend color: '#00FF00', channel: "${slackChannel}", message: "${projectName} Published RC ${publish_version}"
                    } else if (env.BRANCH_NAME == 'master') {
                        slackSend color: '#00FF00', channel: "${slackChannel}", message: ":ok_hand: ${projectName} Master Publish ${publish_version} OK! :ok_hand:"
                    } else if (currentBuild.getPreviousBuild() == null || currentBuild.getPreviousBuild().result == null) {
                        slackSend color: '#00FF00', channel: "${slackChannel}", message: ":ok_hand: ${projectName} build ${currentBuild.currentResult} !!! :ok_hand:\n\tBranch: ${env.BRANCH_NAME}\n(See ${env.BUILD_URL})\n"
                    } else if (currentBuild.getPreviousBuild().result.toString() != 'SUCCESS') {
                            slackSend color: '#00FF00', channel: "${slackChannel}", message: ":ok_hand: ${projectName} Back to Green!! :ok_hand:\n\tBranch: ${env.BRANCH_NAME}\n(See ${env.BUILD_URL})\n"
                    }
                }
            }
        }
    }
}


void npmLogin() {
    sh "echo //registry.npmjs.org/:_authToken=${env.NPM_TOKEN} > .npmrc"
    sh "echo @aml-org:registry=https://registry.npmjs.org/ >> .npmrc"
    sh 'npm config set registry https://registry.npmjs.org/'
    sh 'npm whoami'
}
