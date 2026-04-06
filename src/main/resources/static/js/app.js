// こどちょ アプリケーションスクリプト

// パスワード表示切り替え
function togglePassword(inputId) {
  const input = document.getElementById(inputId || 'password');
  if (input) {
    input.type = input.type === 'password' ? 'text' : 'password';
  }
}
