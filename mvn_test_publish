def call(body) {
    def args = [
        // general agruments
        url: '',
        branch: ''
    ]
    
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = args
    body()
    echo "INFO: ${args.url}"
    echo "INFO: ${args.branch}"

    
    pipline {
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
                        branches: [[ name: agrs.branch ]],
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
        }
    }
}
