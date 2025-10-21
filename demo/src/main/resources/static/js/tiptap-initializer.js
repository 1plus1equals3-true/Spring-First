import { Editor } from '@tiptap/core';
import StarterKit from '@tiptap/starter-kit';

import Image from '@tiptap/extension-image';
import Link from '@tiptap/extension-link';
import { TextStyle } from '@tiptap/extension-text-style'; // Color 확장의 기반
import Color from '@tiptap/extension-color';           // Color 확장

// Tiptap Editor 인스턴스를 전역에서 접근할 수 있도록 선언
window.tiptapEditorInstance = null;

document.addEventListener('DOMContentLoaded', () => {

    window.tiptapEditorInstance = new Editor({
        element: document.querySelector('#tiptap-editor-container'),
        extensions: [
            StarterKit,

            // --- [새 확장 기능 등록 및 설정] ---
            TextStyle, // Color 확장보다 먼저 등록해야 합니다.
            Color.configure({}),

            Image.configure({
                inline: true, // 이미지를 텍스트 흐름에 포함 (줄바꿈 없이)
                allowBase64: true, // 작은 이미지를 인코딩하여 삽입 허용 (개발용)
            }),

            Link.configure({
                openOnClick: false, // 클릭 시 바로 이동 방지 (편집을 위해)
                defaultProtocol: 'https',
            }),
            // ---------------------------------
        ],
        content: '<p>여기에 글을 작성하세요.</p>',
    });

    setupToolbar(window.tiptapEditorInstance);
});

// HTML의 onsubmit 이벤트에서 호출될 전역 함수
// 폼 제출 전에 에디터 내용을 숨겨진 필드에 삽입합니다.
window.setEditorContent = function(event) {
    if (window.tiptapEditorInstance) {
        // Tiptap의 최종 HTML 내용을 추출하여 숨겨진 필드에 설정
        const editorHtml = window.tiptapEditorInstance.getHTML();
        document.getElementById('editor-content-hidden').value = editorHtml;
    }
    // 기존의 파일 유효성 검사를 유지하려면 event.preventDefault() 없이 true를 반환해야 합니다.
    // 하지만 파일 유효성 검사 로직이 이미 write.js의 form.addEventListener('submit', ...)에 있으므로,
    // 이 함수에서는 값만 설정하고 폼 제출을 허용(true)합니다.
    return true;
};


// 툴바 버튼을 만들고 기능을 연결하는 함수
function setupToolbar(editor) {
    const toolbar = document.getElementById('tiptap-toolbar');

    // [기존 항목 유지]
    const actions = [
        { text: 'B', title: '볼드', command: () => editor.chain().focus().toggleBold().run(), isActive: () => editor.isActive('bold') },
        { text: 'I', title: '이탤릭', command: () => editor.chain().focus().toggleItalic().run(), isActive: () => editor.isActive('italic') },
        { text: 'H2', title: '제목', command: () => editor.chain().focus().toggleHeading({ level: 2 }).run(), isActive: () => editor.isActive('heading', { level: 2 }) },
        { text: 'UL', title: '글머리 목록', command: () => editor.chain().focus().toggleBulletList().run(), isActive: () => editor.isActive('bulletList') },
        { text: 'Quote', title: '인용', command: () => editor.chain().focus().toggleBlockquote().run(), isActive: () => editor.isActive('blockquote') },

        // --- [새 확장 기능 버튼 추가] ---
        { text: 'Link', title: '링크 삽입', command: () => setLink(editor), isActive: () => editor.isActive('link') },
        { text: 'Img', title: '이미지 삽입', command: () => setImage(editor) },
        // 빨강 (Red)
        { text: '🔴', title: '빨강', command: () => editor.chain().focus().setColor('#FF0000').run(), isActive: () => editor.isActive('textStyle', { color: '#FF0000' }) },
        // 주황 (Orange)
        { text: '🟠', title: '주황', command: () => editor.chain().focus().setColor('#FFA500').run(), isActive: () => editor.isActive('textStyle', { color: '#FFA500' }) },
        // 노랑 (Yellow)
        { text: '🟡', title: '노랑', command: () => editor.chain().focus().setColor('#FFFF00').run(), isActive: () => editor.isActive('textStyle', { color: '#FFFF00' }) },
        // 초록 (Green)
        { text: '🟢', title: '초록', command: () => editor.chain().focus().setColor('#008000').run(), isActive: () => editor.isActive('textStyle', { color: '#008000' }) },
        // 파랑 (Blue)
        { text: '🔵', title: '파랑', command: () => editor.chain().focus().setColor('#0000FF').run(), isActive: () => editor.isActive('textStyle', { color: '#0000FF' }) },
        // 남색 (Indigo)
        { text: ' indigo', title: '남색', command: () => editor.chain().focus().setColor('#4B0082').run(), isActive: () => editor.isActive('textStyle', { color: '#4B0082' }) },
        // 보라 (Violet/Purple)
        { text: '🟣', title: '보라', command: () => editor.chain().focus().setColor('#EE82EE').run(), isActive: () => editor.isActive('textStyle', { color: '#EE82EE' }) },
        // 색상 초기화 (Clear Color)
        { text: 'Clr', title: '색상 초기화', command: () => editor.chain().focus().unsetColor().run(), isActive: () => !editor.isActive('color') },
        // ---------------------------------
    ];

    actions.forEach(action => {
        const button = document.createElement('button');
        button.textContent = action.text;
        button.title = action.title;
        button.type = 'button';
        button.onclick = action.command;

        // 버튼 활성화 상태 표시를 위한 리스너
        editor.on('selectionUpdate', () => {
            if (action.isActive) {
                if (action.isActive()) {
                    button.classList.add('is-active');
                } else {
                    button.classList.remove('is-active');
                }
            }
        });
        toolbar.appendChild(button);
    });
}

// --- [링크 삽입을 위한 Helper 함수] ---
function setLink(editor) {
    const previousUrl = editor.getAttributes('link').href;
    const url = window.prompt('URL을 입력하세요:', previousUrl);

    // 사용자가 취소했을 경우
    if (url === null) {
        return;
    }

    // URL이 비어있으면 링크 제거
    if (url === '') {
        editor.chain().focus().extendMarkRange('link').unsetLink().run();
        return;
    }

    // 새 URL로 링크 설정
    editor.chain().focus().extendMarkRange('link').setLink({ href: url }).run();
}

// --- [이미지 삽입을 위한 Helper 함수] ---
function setImage(editor) {
    const url = window.prompt('이미지 URL을 입력하세요:');

    if (url) {
        editor.chain().focus().setImage({ src: url }).run();
    }
}