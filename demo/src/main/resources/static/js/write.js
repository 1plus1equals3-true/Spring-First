const MAX_FILES = 5; //최대 파일 개수
const fileUploadButton = document.getElementById('fileUploadButton');
const hiddenFileInput = document.getElementById('hiddenFileInput');
const fileListDiv = document.getElementById('fileList');
const errorMessageDiv = document.getElementById('errorMessage');
const form = document.querySelector('.write-form');

// 1. 커스텀 버튼 클릭 시 숨겨진 파일 입력창 클릭 이벤트 발생
fileUploadButton.addEventListener('click', () => {
    hiddenFileInput.click();
});

// 2. 파일 선택 시 이벤트 처리
hiddenFileInput.addEventListener('change', (e) => {
    const files = e.target.files;
    errorMessageDiv.textContent = ''; // 오류 메시지 초기화
    fileListDiv.innerHTML = ''; // 파일 목록 초기화

    if (files.length === 0) {
        fileListDiv.textContent = '선택된 파일 없음';
        return;
    }

    // 3. 파일 개수 제한 로직
    if (files.length > MAX_FILES) {
        errorMessageDiv.textContent = `파일은 최대 ${MAX_FILES}개까지만 선택할 수 있습니다.`;

        // 파일 개수 초과 시 선택된 파일을 초기화 (선택 취소)
        // 이를 위해 input type=file의 value를 초기화합니다.
        e.target.value = '';
        fileListDiv.textContent = '선택된 파일 없음';
        return;
    }

    // 4. 허용된 개수일 경우 목록 표시
    const fragment = document.createDocumentFragment();
    for (let i = 0; i < files.length; i++) {
        const fileName = files[i].name;
        const item = document.createElement('span');
        item.className = 'file-item';
        // 파일 이름을 목록에 추가합니다.
        item.textContent = `✅ ${fileName}`;
        fragment.appendChild(item);
    }
    fileListDiv.appendChild(fragment);
});

// 5. 폼 제출 시 최종 확인 (선택 사항)
form.addEventListener('submit', (e) => {
    if (hiddenFileInput.files.length > MAX_FILES) {
        alert(`제출할 수 없습니다. 파일은 최대 ${MAX_FILES}개까지만 허용됩니다.`);
        e.preventDefault(); // 제출 방지
    }
});