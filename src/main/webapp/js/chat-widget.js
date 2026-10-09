/* Floating Chatbot Widget for SameerMart */
(function() {
    'use strict';

    document.addEventListener('DOMContentLoaded', initChatWidget);

    function initChatWidget() {
        if (document.getElementById('sameermart-chat-root')) return;

        const root = document.createElement('div');
        root.id = 'sameermart-chat-root';

        // Launcher button
        const launcher = document.createElement('button');
        launcher.id = 'sameermart-chat-launcher';
        launcher.setAttribute('aria-label', 'Open SameerMart Assistant');
        launcher.innerHTML = `
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
        `;

        // Chat Container
        const container = document.createElement('div');
        container.id = 'sameermart-chat-container';
        container.style.display = 'none';

        // Header
        const header = document.createElement('div');
        header.className = 'chat-header';
        
        const titleSpan = document.createElement('span');
        titleSpan.textContent = 'SameerMart Assistant';
        header.appendChild(titleSpan);

        const closeBtn = document.createElement('button');
        closeBtn.className = 'chat-close-btn';
        closeBtn.textContent = '×';
        closeBtn.setAttribute('aria-label', 'Close chat');
        closeBtn.addEventListener('click', toggleChat);
        header.appendChild(closeBtn);

        // Body / Message log
        const messagesDiv = document.createElement('div');
        messagesDiv.className = 'chat-messages';
        messagesDiv.id = 'chat-messages-log';

        // FAQ Quick Prompts
        const quickPromptsDiv = document.createElement('div');
        quickPromptsDiv.className = 'chat-quick-prompts';
        const faqs = ['How to order?', 'Payment details', 'Return policy', 'Order status'];
        faqs.forEach(q => {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'quick-prompt-btn';
            btn.textContent = q;
            btn.addEventListener('click', () => {
                inputField.value = q;
                form.dispatchEvent(new Event('submit'));
            });
            quickPromptsDiv.appendChild(btn);
        });

        // Input Form
        const form = document.createElement('form');
        form.className = 'chat-input-form';
        
        const inputField = document.createElement('input');
        inputField.type = 'text';
        inputField.className = 'chat-input-field';
        inputField.placeholder = 'Ask about products, orders, returns...';
        inputField.maxLength = 500;
        inputField.required = true;

        const sendBtn = document.createElement('button');
        sendBtn.type = 'submit';
        sendBtn.className = 'chat-send-btn';
        sendBtn.textContent = 'Send';

        form.appendChild(inputField);
        form.appendChild(sendBtn);

        container.appendChild(header);
        container.appendChild(messagesDiv);
        container.appendChild(quickPromptsDiv);
        container.appendChild(form);

        root.appendChild(launcher);
        root.appendChild(container);
        document.body.appendChild(root);

        // Add greeting message safely using textContent
        appendBotMessage("Hello! I'm SameerMart Assistant. How can I help you with your shopping or orders today?");

        launcher.addEventListener('click', toggleChat);

        function toggleChat() {
            const isHidden = container.style.display === 'none';
            container.style.display = isHidden ? 'flex' : 'none';
            if (isHidden) {
                inputField.focus();
            }
        }

        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            const text = inputField.value.trim();
            if (!text) return;

            appendUserMessage(text);
            inputField.value = '';
            inputField.disabled = true;
            sendBtn.disabled = true;

            const loadingIndicator = appendBotMessage("Thinking...");

            try {
                // Determine context path from window.location
                const pathname = window.location.pathname;
                let contextPath = "";
                if (pathname.startsWith('/SameerMart')) {
                    contextPath = '/SameerMart';
                }

                // Retrieve CSRF token from page if present
                const csrfInput = document.querySelector('input[name="csrfToken"]');
                const csrfToken = csrfInput ? csrfInput.value : '';

                const response = await fetch(`${contextPath}/api/v1/chat`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-Token': csrfToken
                    },
                    body: JSON.stringify({
                        message: text,
                        csrfToken: csrfToken
                    })
                });

                const result = await response.json();
                loadingIndicator.remove();

                if (result.success && result.data && result.data.reply) {
                    appendBotMessage(result.data.reply);
                } else if (result.error && result.error.message) {
                    appendBotMessage("Note: " + result.error.message);
                } else {
                    appendBotMessage("Our assistant is unavailable right now. Please try again later.");
                }
            } catch (err) {
                loadingIndicator.remove();
                appendBotMessage("Connection error. Please try again.");
            } finally {
                inputField.disabled = false;
                sendBtn.disabled = false;
                inputField.focus();
            }
        });

        function appendUserMessage(text) {
            const msgEl = document.createElement('div');
            msgEl.className = 'chat-bubble chat-bubble-user';
            msgEl.textContent = text; // Safe textContent (prevents XSS)
            messagesDiv.appendChild(msgEl);
            messagesDiv.scrollTop = messagesDiv.scrollHeight;
        }

        function appendBotMessage(text) {
            const msgEl = document.createElement('div');
            msgEl.className = 'chat-bubble chat-bubble-bot';
            msgEl.textContent = text; // Safe textContent (prevents XSS)
            messagesDiv.appendChild(msgEl);
            messagesDiv.scrollTop = messagesDiv.scrollHeight;
            return msgEl;
        }
    }
})();
