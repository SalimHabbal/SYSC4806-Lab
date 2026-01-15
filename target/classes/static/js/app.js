// Lab6: Lightweight SPA enhancements layered onto the existing server-rendered pages.
// Lab6 part 2: All SPA behaviour lives here, progressively enhancing the Lab 6 Part 1 templates.
(function () {
  document.addEventListener('DOMContentLoaded', function () {
    enhanceCreateBook();
    enhanceAddBuddy();
  });

  function enhanceCreateBook() {
    const form = document.querySelector('[data-enhance="create-book"]');
    if (!form) return;

    const card = form.closest('.card') || document.body;
    // Lab6 part 2: DOM handles for JS-controlled flash messaging and dynamic tables.
    const messageEl = ensureListMessage(card, 'message');
    const errorEl = ensureListMessage(card, 'error');
    const emptyEl = card.querySelector('[data-empty-books]');
    let table = card.querySelector('[data-books-table]');

    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      clearMessage(messageEl);
      clearMessage(errorEl);

      try {
        // Lab6 part 2: Create address books via REST API and update DOM without reload.
        const response = await fetch('/api/addressbooks', {
          method: 'POST',
          headers: { Accept: 'application/json' }
        });

        if (!response.ok) throw new Error('Request failed');

        const book = await response.json();
        table = ensureBooksTable(card, table, emptyEl);
        appendBookRow(table, book);
        if (emptyEl) emptyEl.style.display = 'none';
        showMessage(messageEl, `Created address book #${book.id}`);
      } catch (error) {
        showMessage(errorEl, 'Unable to create address book right now. Falling back to full page load…');
        form.submit();
      }
    });
  }

  function enhanceAddBuddy() {
    const form = document.querySelector('[data-enhance="add-buddy"]');
    if (!form) return;

    const card = form.closest('[data-book-id]');
    if (!card) return;

    const bookId = card.getAttribute('data-book-id');
    if (!bookId) return;

    // Lab6 part 2: Message blocks surfaced via data attributes for JS feedback.
    const successEl = ensureDetailMessage(card, 'success');
    const errorEl = ensureDetailMessage(card, 'error');
    const infoEl = ensureDetailMessage(card, 'message'); // reused for informational feedback

    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      clearMessage(successEl);
      clearMessage(errorEl);
      clearMessage(infoEl);

      const formData = new FormData(form);
      const payload = {
        name: formData.get('name')?.toString().trim(),
        phone: formData.get('phone')?.toString().trim(),
        address: formData.get('address')?.toString().trim()
      };

      if (!payload.name || !payload.phone) {
        showMessage(errorEl, 'Name and phone are required.');
        return;
      }

      if (payload.address === '') {
        payload.address = null;
      }

      try {
        // Lab6 part 2: POST buddy info as JSON; re-render the table with fresh REST data.
        const response = await fetch(`/api/addressbooks/${bookId}/buddies`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json'
          },
          body: JSON.stringify(payload)
        });

        if (!response.ok) {
          const text = await response.text();
          throw new Error(text || 'Request failed');
        }

        const updatedBook = await response.json();
        renderBuddies(card, updatedBook.buddies || []);
        form.reset();
        const nameField = form.querySelector('input[name="name"]');
        if (nameField) nameField.focus();
        showMessage(successEl, `Added ${payload.name} to the address book.`);
      } catch (error) {
        showMessage(errorEl, 'Unable to add buddy. Falling back to full page load…');
        form.submit();
      }
    });
  }

  function ensureBooksTable(card, existingTable, emptyEl) {
    if (existingTable) {
      existingTable.style.display = '';
      return existingTable;
    }

    const table = document.createElement('table');
    table.setAttribute('data-books-table', '');

    const thead = document.createElement('thead');
    const headRow = document.createElement('tr');
    ['ID', 'Buddies', 'View'].forEach((text) => {
      const th = document.createElement('th');
      th.textContent = text;
      headRow.appendChild(th);
    });
    thead.appendChild(headRow);

    const tbody = document.createElement('tbody');

    table.appendChild(thead);
    table.appendChild(tbody);

    const insertBeforeTarget = emptyEl && emptyEl.parentElement === card ? emptyEl : null;
    card.insertBefore(table, insertBeforeTarget);
    return table;
  }

  function appendBookRow(table, book) {
    const tbody = table.querySelector('tbody') || table.appendChild(document.createElement('tbody'));

    // Avoid duplicate rows if a book already exists.
    const existing = tbody.querySelector(`tr[data-book-row="${book.id}"]`);
    if (existing) existing.remove();

    const row = document.createElement('tr');
    row.setAttribute('data-book-row', book.id);

    const idCell = document.createElement('td');
    idCell.textContent = book.id;

    const countCell = document.createElement('td');
    const buddyCount = Array.isArray(book.buddies) ? book.buddies.length : 0;
    countCell.textContent = buddyCount;

    const linkCell = document.createElement('td');
    const link = document.createElement('a');
    link.className = 'btn-link';
    link.href = `/addressbooks/${book.id}`;
    link.textContent = 'Open';
    linkCell.appendChild(link);

    row.appendChild(idCell);
    row.appendChild(countCell);
    row.appendChild(linkCell);
    tbody.appendChild(row);
  }

  function renderBuddies(card, buddies) {
    const emptyEl = card.querySelector('[data-empty-buddies]');
    let table = card.querySelector('[data-buddies-table]');

    if (!table) {
      table = document.createElement('table');
      table.setAttribute('data-buddies-table', '');

      const thead = document.createElement('thead');
      const headRow = document.createElement('tr');
      ['ID', 'Name', 'Phone', 'Address'].forEach((text) => {
        const th = document.createElement('th');
        th.textContent = text;
        headRow.appendChild(th);
      });
      thead.appendChild(headRow);

      const tbody = document.createElement('tbody');
      table.appendChild(thead);
      table.appendChild(tbody);

      const sectionHeading = card.querySelector('.section');
      card.insertBefore(table, sectionHeading ? sectionHeading.nextElementSibling : emptyEl);
    }

    const tbody = table.querySelector('tbody') || table.appendChild(document.createElement('tbody'));
    tbody.innerHTML = '';

    buddies.forEach((buddy) => {
      const row = document.createElement('tr');

      const idCell = document.createElement('td');
      idCell.textContent = buddy.id ?? '';

      const nameCell = document.createElement('td');
      nameCell.textContent = buddy.name ?? '';

      const phoneCell = document.createElement('td');
      phoneCell.textContent = buddy.phone ?? '';

      const addressCell = document.createElement('td');
      addressCell.textContent = buddy.address ?? '';

      row.appendChild(idCell);
      row.appendChild(nameCell);
      row.appendChild(phoneCell);
      row.appendChild(addressCell);
      tbody.appendChild(row);
    });

    if (Array.isArray(buddies) && buddies.length > 0) {
      table.style.display = '';
      if (emptyEl) emptyEl.style.display = 'none';
    } else {
      table.style.display = 'none';
      if (emptyEl) emptyEl.style.display = '';
    }
  }

  function ensureListMessage(card, type) {
    const selector = type === 'error' ? '[data-list-error]' : '[data-list-message]';
    let el = card.querySelector(selector);
    if (el) return el;

    el = document.createElement('div');
    el.className = `alert ${type === 'error' ? 'error' : 'info'}`;
    el.setAttribute(type === 'error' ? 'data-list-error' : 'data-list-message', '');
    el.style.display = 'none';
    card.insertBefore(el, card.firstChild);
    return el;
  }

  function ensureDetailMessage(card, type) {
    const selectorMap = {
      success: '[data-detail-success]',
      error: '[data-detail-error]',
      message: '[data-detail-message]'
    };
    const classMap = {
      success: 'alert success',
      error: 'alert error',
      message: 'alert info'
    };
    const attributeMap = {
      success: 'data-detail-success',
      error: 'data-detail-error',
      message: 'data-detail-message'
    };

    let el = card.querySelector(selectorMap[type]);
    if (el) return el;

    el = document.createElement('div');
    el.className = classMap[type];
    el.setAttribute(attributeMap[type], '');
    el.style.display = 'none';

    const header = card.querySelector('.header');
    if (header) {
      header.insertAdjacentElement('afterend', el);
    } else {
      card.insertBefore(el, card.firstChild);
    }
    return el;
  }

  function showMessage(el, text) {
    if (!el) return;
    el.textContent = text;
    el.style.display = '';
  }

  function clearMessage(el) {
    if (!el) return;
    el.textContent = '';
    el.style.display = 'none';
  }
})();
