def call() {
  sh '''
  echo Welcome to Futuretech 2024
  hostname -i
  pwd
  whoami
  cat /etc/os-release
  nproc
  free -m
  '''
}
