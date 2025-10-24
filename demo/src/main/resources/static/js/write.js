// write.js

const MAX_FILES = 5; //최대 파일 개수
// 모든 파일 관련 요소를 찾습니다.
const fileUploadButton = document.getElementById('fileUploadButton');
const hiddenFileInput = document.getElementById('hiddenFileInput');
const fileListDiv = document.getElementById('fileList');
const errorMessageDiv = document.getElementById('errorMessage');
const form = document.querySelector('.write-form');


// ★★★ 핵심: fileUploadButton (글 작성/수정 페이지 모두에 존재하는 버튼 ID)이 있을 때만 로직을 실행 ★★★
if (fileUploadButton && hiddenFileInput && fileListDiv) {

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
                    e.target.value = ''; // 파일 선택 취소
                    fileListDiv.textContent = '선택된 파일 없음';
                    return;
            }

            // 4. 허용된 개수일 경우 목록 표시
            const fragment = document.createDocumentFragment();
                for (let i = 0; i < files.length; i++) {
                    const fileName = files[i].name;
                    const item = document.createElement('span');
                    item.className = 'file-item';
                    item.textContent = `✅ ${fileName}`;
                    fragment.appendChild(item);
            }
            fileListDiv.appendChild(fragment);
    });

    // 5. 폼 제출 시 최종 확인 (파일 관련 유효성 검사)
    // form이 null이 아닐 때만 실행합니다.
    if (form) {
        form.addEventListener('submit', (e) => {
                if (hiddenFileInput.files.length > MAX_FILES) {
                        alert(`제출할 수 없습니다. 파일은 최대 ${MAX_FILES}개까지만 허용됩니다.`);
                        e.preventDefault(); // 제출 방지
                }
        });
    }
}
// ★★★ 이 if 블록 밖으로 나가면 modify.html에서는 이 파일 관련 로직이 실행되지 않아 에러가 나지 않습니다.