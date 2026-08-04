pipeline {
    agent any

    triggers {
        githubPush()
    }
    
    parameters {
        string(name: 'EC2_HOST', defaultValue: 'ec2-13-220-102-244.compute-1.amazonaws.com')
        string(name: 'EC2_USER', defaultValue: 'ubuntu')
    }

    environment {
        SSH_CRED_ID = 'springboot-app'
        IMAGE_NAME = "demo-hello-world"
        CONTAINER_NAME = "demo-hello-world-container"
        APP_PORT = "8080"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify SSH Connectivity') {
            steps {
                sshagent(credentials: ["${SSH_CRED_ID}"]) {
                    sh "ssh -o StrictHostKeyChecking=no ${params.EC2_USER}@${params.EC2_HOST} 'echo Connected successfully'"
                }
            }
        }

        stage('Check Docker Installed') {
            steps {
                script {
                    def dockerCheck = sh(script: 'command -v docker', returnStatus: true)
                    if (dockerCheck != 0) {
                        error("Docker is not installed on this EC2 instance. Please install Docker before running this pipeline.")
                    } else {
                        sh 'docker --version'
                        echo "Docker is installed. Proceeding to build."
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }

        stage('Stop & Remove Existing Container') {
            steps {
                script {
                    sh """
                        if [ \$(docker ps -aq -f name=${CONTAINER_NAME}) ]; then
                            docker rm -f ${CONTAINER_NAME}
                        fi
                    """
                }
            }
        }

        stage('Run Container') {
            steps {
                sh "docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:8080 ${IMAGE_NAME}"
            }
        }

        stage('Verify Deployment') {
            steps {
                sh "sleep 5"
                sh "docker ps -f name=${CONTAINER_NAME}"
            }
        }
    }

    post {
        success {
            echo "Deployment successful! App is running on port ${APP_PORT}."
        }
        failure {
            echo "Pipeline failed. Check the logs above for details."
        }
    }
}