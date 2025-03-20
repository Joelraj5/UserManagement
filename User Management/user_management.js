document.addEventListener("DOMContentLoaded", function () {
    fetchUsers();

    document.getElementById("userForm").addEventListener("submit", function (event) {
        event.preventDefault();
        let id = document.getElementById("userId").value;
        let name = document.getElementById("userName").value;
        let email = document.getElementById("userEmail").value;
        let action = id ? "update" : "create";

        fetch("UserServlet", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `action=${action}&id=${id}&name=${name}&email=${email}`
        }).then(() => {
            fetchUsers();
            document.getElementById("userForm").reset();
        });
    });
});

function fetchUsers() {
    fetch("UserServlet").then(response => response.json()).then(data => {
        let tbody = document.getElementById("userTableBody");
        tbody.innerHTML = "";
        data.forEach(user => {
            let row = `<tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>
                    <button onclick="editUser(${user.id}, '${user.name}', '${user.email}')">Edit</button>
                    <button onclick="deleteUser(${user.id})">Delete</button>
                </td>
            </tr>`;
            tbody.innerHTML += row;
        });
    });
}

function editUser(id, name, email) {
    document.getElementById("userId").value = id;
    document.getElementById("userName").value = name;
    document.getElementById("userEmail").value = email;
}

function deleteUser(id) {
    fetch("UserServlet", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `action=delete&id=${id}`
    }).then(() => fetchUsers());
}
