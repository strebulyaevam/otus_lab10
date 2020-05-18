pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                script {
                    env.git_branch = checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '37879241-36f1-47cd-be99-1df64a470903', url: 'https://github.com/strebulyaevam/otus_lab9']]]).GIT_BRANCH
                }
            }
        }

        stage('Run tests') {
            steps {
                script {
                    bat 'mvn clean test'
                }
            }
        }
    }
    
post {
       always  {
           script{
//                junit allowEmptyResults: true, testResults: '**/target/*.xml'
//                emailext subject: "Build report: Project name -&gt; ${env.JOB_NAME}", body: "&lt;b&gt;Tests Results&lt;/b&gt;&lt;br&gt;Project: ${currentBuild.projectName} &lt;br&gt;Build Number: ${env.BUILD_NUMBER} &lt;br&gt;Status:${currentBuild.currentResult} &lt;br&gt;Branch: ${env.git_branch} &lt;br&gt;Duration: ${currentBuild.durationString}}", mimeType: 'text/html', to: '$DEFAULT_RECIPIENTS'
                
                emailext subject: "Build report: Project name -&gt; ${env.JOB_NAME}", body: "&lt;b&gt;Tests Results&lt;/b&gt;&lt;br&gt;Project: ${currentBuild.projectName} &lt;br&gt;Build Number: ${env.BUILD_NUMBER} &lt;br&gt;Status:${currentBuild.currentResult} &lt;br&gt;Branch: ${env.git_branch} &lt;br&gt;Duration: ${currentBuild.durationString} &lt;br&gt;Test_total: ${TEST_COUNTS(var:'total')} &lt;br&gt;Test_pass: ${TEST_COUNTS(var:'pass')} &lt;br&gt;Test_failed: ${TEST_COUNTS(var:'fail')} &lt;br&gt;Test_skipped: ${TEST_COUNTS(var:'skip')}", mimeType: 'text/html', to: '$DEFAULT_RECIPIENTS'

                slackSend channel: '#jenkins-rep', message: 'New build is ready. Check your e-mail for details.'
               allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
              ])
           }
       }
    }
}