document.addEventListener("DOMContentLoaded", function() {
  const role = sessionStorage.getItem('simulated_role') || 'customer';
  
  const bar = document.createElement('div');
  bar.className = 'role-simulator-bar py-2';
  bar.style.backgroundColor = '#111215';
  bar.style.borderBottom = '2px solid var(--accent, #E50914)';
  bar.innerHTML = `
    <div class="container d-flex justify-content-between align-items-center flex-wrap gap-2">
      <div class="d-flex align-items-center gap-2">
        <span class="badge bg-danger">MÔ PHỎNG VAI TRÒ</span>
        <small class="text-white-50">Chọn vai trò dưới đây để hiển thị UI tương ứng</small>
      </div>
      <div class="d-flex gap-2">
        <button class="role-badge-btn ${role === 'customer' ? 'active-customer' : ''}" onclick="setSimulatedRole('customer')">
          <i class="bi bi-people-fill me-1"></i>Khách Hàng
        </button>
        <button class="role-badge-btn ${role === 'staff' ? 'active-staff' : ''}" onclick="setSimulatedRole('staff')">
          <i class="bi bi-shield-lock-fill me-1"></i>Nhân Viên (Staff)
        </button>
        <button class="role-badge-btn ${role === 'admin' ? 'active-admin' : ''}" onclick="setSimulatedRole('admin')">
          <i class="bi bi-sliders me-1"></i>Quản Trị (Admin)
        </button>
      </div>
    </div>
  `;
  document.body.insertBefore(bar, document.body.firstChild);

  // Apply visibility classes based on role
  applyRoleVisibility(role);
});

function setSimulatedRole(role) {
  sessionStorage.setItem('simulated_role', role);
  const currentPath = window.location.pathname;
  if (role === 'admin' && !currentPath.includes('dashboard') && !currentPath.includes('crud')) {
    window.location.href = 'dashboard.html';
  } else if ((role === 'customer' || role === 'staff') && (currentPath.includes('dashboard') || currentPath.includes('crud'))) {
    window.location.href = 'home.html';
  } else {
    window.location.reload();
  }
}

function applyRoleVisibility(role) {
  document.querySelectorAll('[data-role]').forEach(el => {
    const allowedRoles = el.getAttribute('data-role').split(',');
    if (allowedRoles.includes(role)) {
      el.style.setProperty('display', '', 'important');
    } else {
      el.style.setProperty('display', 'none', 'important');
    }
  });

  // Also enable/disable conditional links or components
  document.querySelectorAll('.role-badge-btn').forEach(btn => {
    btn.classList.remove('active-customer', 'active-staff', 'active-admin');
  });
  const activeBtn = document.querySelector(`.role-badge-btn:nth-child(${role === 'customer' ? 1 : role === 'staff' ? 2 : 3})`);
  if (activeBtn) {
    activeBtn.classList.add(`active-${role}`);
  }
}
