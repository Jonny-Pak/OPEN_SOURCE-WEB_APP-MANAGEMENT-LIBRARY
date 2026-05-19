const { Client } = require('pg');

async function checkUser() {
    const client = new Client({
        user: 'postgres',
        host: 'localhost',
        database: 'quanlythuvien',
        password: '123456',
        port: 5432,
    });

    try {
        await client.connect();
        const res = await client.query("SELECT email, ho_dem, ten, so_dien_thoai FROM nguoi_dung WHERE email = 'thuanln1907@gmail.com'");
        console.log(res.rows);
    } catch (err) {
        console.error(err);
    } finally {
        await client.end();
    }
}

checkUser();
