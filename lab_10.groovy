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
                testng '**/testng-results.xml'
                emailext subject: "Build report: Project name -> ${env.JOB_NAME}", body: "<b>Tests Results</b><br>Project: ${currentBuild.projectName} <br>Build Number: ${env.BUILD_NUMBER} <br>Status:${currentBuild.currentResult} <br>Branch: ${env.git_branch} <br>Duration: ${currentBuild.durationString} <br>Test_total: ${env.test_total}", mimeType: 'text/html', to: '$DEFAULT_RECIPIENTS'         
                slackSend channel: '#jenkins-rep', message: 'New build is ready. Check your e-mail for details.'
           }
       }
    }
}