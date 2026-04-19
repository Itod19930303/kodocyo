// こどちょ アプリケーションスクリプト

// パスワード表示切り替え
function togglePassword(inputId) {
  const input = document.getElementById(inputId || 'password');
  if (input) {
    input.type = input.type === 'password' ? 'text' : 'password';
  }
}

// チップ選択（sharing, transaction/form）
function selectChip(btn, inputId, value) {
  const parent = btn.parentElement;
  parent.querySelectorAll('.chip, .category-chip').forEach(c => c.classList.remove('selected'));
  btn.classList.add('selected');
  document.getElementById(inputId).value = value;
}

// トグル可能チップ選択（goal/form - 再クリックで解除可能）
function selectToggleChip(btn, containerId, inputId, value) {
  const allBtns = document.querySelectorAll('#' + containerId + ' .chip');
  const input = document.getElementById(inputId);
  if (btn.classList.contains('selected')) {
    btn.classList.remove('selected');
    input.value = '';
  } else {
    allBtns.forEach(b => b.classList.remove('selected'));
    btn.classList.add('selected');
    input.value = value;
  }
}

// 絵文字選択（goal/form）
function selectEmoji(btn, emoji) {
  document.querySelectorAll('.emoji-btn').forEach(b => b.classList.remove('selected'));
  btn.classList.add('selected');
  document.getElementById('emojiInput').value = emoji;
}

// URL コピー（sharing）
function copyUrl() {
  const input = document.getElementById('inviteUrl');
  input.select();
  navigator.clipboard.writeText(input.value).then(() => {
    alert('招待URLをコピーしました');
  });
}
