def call(body) {
    def args = [
        // general agruments
        url: '',
        branch: '',
        sonar_scanner: '',
        sonar_server: '',
        projectKey: '',
        timeout_time: '',
        timeout_unit: ''
    ]
    
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = args
    body()
    echo "INFO: ${args.url}"
    echo "INFO: ${args.branch}"

    
    pipeline {
        agent any
        tools {
            maven 'Maven-360' 
            jdk 'JAVA-11' 
        }
        stages {
            stage('Git Checkout') {
                steps {
                    checkout([
                    $class : 'GitSCM',
                        branches: [[ name: args.branch ]],
                        userRemoteConfigs: [[ url: args.url ]]
                    ])
                }
            }
            stage ('Test') {
                steps {
                    sh 'mvn test'
                }
            }
            stage ('Build') {
                steps {
                    sh 'mvn -Dmaven.test.failure.ignore=true install'
                }
                post {
                    success {
                        junit(
                            allowEmptyResults: true,
                            testResults: '**/target/surefire-reports/*.xml'
                        )
                    }
                }
            }
            stage ('Code Quality') {
            environment {
                scannerHome = tool "${args.sonar_scanner}"
            }
            steps {
                withSonarQubeEnv("${args.sonar_server}") {
                    sh "${scannerHome}/bin/sonar-scanner \
                    -D sonar.projectKey=${args.projectKey} \
                    -D sonar.exclusions=vendor/**,resources/**,**/*.java"
                }
            }
            }
            stage('SonarQube Quality Gates Check'){
            steps {
                timeout(time: "${args.timeout_time}", unit: "${args.timeout_unit}") {
                    waitForQualityGate abortPipeline: true
                }
            }
            }
        }
    }
}
