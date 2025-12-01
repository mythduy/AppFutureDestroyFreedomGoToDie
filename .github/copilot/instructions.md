# 🧠 INSTRUCTION CHUNG CHO COPILOT & DEV — ELECTRONICS STORE

**Mục đích:** Đây là file quy tắc bắt buộc khi yêu cầu Copilot hoặc dev tạo/sửa code cho project.  
**Vị trí:** lưu ở `./instructions.md` (root) hoặc `docs/instruction.md`.

## Nguyên tắc chung
- Luôn trả lời tôi bằng tiếng Việt.
- Comment bằng **Tiếng Việt** (ngắn gọn, rõ ràng).
- Tên class/variable/method **tiếng Anh** theo Java conventions (camelCase/PascalCase).
- Kiến trúc: **MVVM + Repository + Room** (mặc định). Nếu task yêu cầu server, sẽ dùng Spring Boot — xem `springboot-backend.md`.

## Output yêu cầu
- Trả **full file** hoặc **git-style patch** (không chỉ snippet).
- Mỗi file Java có header comment 1-2 dòng tiếng Việt mô tả chức năng.
- Nếu thay đổi nhiều file: trả theo module (models/, dao/, repository/, viewmodel/, utils/).

## Kiểm tra trước khi trả
- Code phải **compile** (nếu có dependency, nêu rõ).
- Không để log sensitive (mật khẩu,…).
- Nếu thay đổi UI, kèm checklist test (3-5 bước).

## Git
- Branch: `feature/<short-desc>` hoặc `fix/<short-desc>`.
- Commit message mẫu:
  - `feat(product): add product gallery from assets`
  - `fix(cart): correct total calculation`

## Khi gặp ambiguity
- Hỏi 1 câu duy nhất; nếu không trả lời, mặc định: Room + MVVM local.

---
