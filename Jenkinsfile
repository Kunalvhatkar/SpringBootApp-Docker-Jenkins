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
        SSH_CRED_ID   = 'springboot-app'
        IMAGE_NAME    = "demo-hello-world"
        CONTAINER_NAME = "demo-hello-world-container"
        APP_PORT      = "8080"
        REPO_URL      = "https://github.com/Kunalvhatkar/SpringBootApp-Docker-Jenkins.git"
        REMOTE_DIR    = "/home/${params.EC2_USER}/springbootapp-deployment"
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
                sshagent(credentials: ["${SSH_CRED_ID}"]) {
                    script {
                        def dockerCheck = sh(
                            script: "ssh -o StrictHostKeyChecking=no ${params.EC2_USER}@${params.EC2_HOST} 'command -v docker'",
                            returnStatus: true
                        )
                        if (dockerCheck != 0) {
                            error("Docker is not installed on this EC2 instance. Please install Docker before running this pipeline.")
                        } else {
                            sh "ssh -o StrictHostKeyChecking=no ${params.EC2_USER}@${params.EC2_HOST} 'sudo docker --version'"
                            echo "Docker is installed. Proceeding to build."
                        }
                    }
                }
            }
        }

        stage('Sync Code to EC2') {
            steps {
                sshagent(credentials: ["${SSH_CRED_ID}"]) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${params.EC2_USER}@${params.EC2_HOST} '
                            if [ -d "${REMOTE_DIR}" ]; then
                                cd ${REMOTE_DIR} && git pull
                            else
                                git clone ${REPO_URL} ${REMOTE_DIR}
                            fi
                        '
                    """
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sshagent(credentials: ["${SSH_CRED_ID}"]) {
                    sh "ssh -o StrictHostKeyChecking=no ${params.EC2_USER}@${params.EC2_HOST} 'cd ${REMOTE_DIR} && sudo docker build -t ${IMAGE_NAME} .'"
                }
            }
        }

        stage('Stop & Remove Existing Container') {
            steps {
                sshagent(credentials: ["${SSH_CRED_ID}"]) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${params.EC2_USER}@${params.EC2_HOST} '
                            if [ \$(sudo docker ps -aq -f name=${CONTAINER_NAME}) ]; then
                                sudo docker rm -f ${CONTAINER_NAME}
                            fi
                        '
                    """
                }
            }
        }

        stage('Run Container') {
            steps {
                sshagent(credentials: ["${SSH_CRED_ID}"]) {
                    sh "ssh -o StrictHostKeyChecking=no ${params.EC2_USER}@${params.EC2_HOST} 'sudo docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:8080 ${IMAGE_NAME}'"
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sshagent(credentials: ["${SSH_CRED_ID}"]) {
                    sh "sleep 5"
                    sh "ssh -o StrictHostKeyChecking=no ${params.EC2_USER}@${params.EC2_HOST} 'sudo docker ps -f name=${CONTAINER_NAME}'"
                }
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