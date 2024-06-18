def call() {
  sh '''
  echo Welcome to Futuretech 2024
  hostname -i
  pwd
  whoami
  nproc
  free -m
  curl -vvv https://google.com
  '''
}
