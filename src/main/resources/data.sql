INSERT INTO users (id, email, nombre, apellidos, foto_perfil, fecha_nacimiento) VALUES
('a3f1c7c0-d96f-4b4f-8f84-123456789abc', 'kaushd@kjhasd.com', 'Tomas', 'Gomez', 'https://example.com/foto.jpg', '1995-04-22'),
('b7e2d1a3-8bcb-4e5a-bfd4-987654321def', 'alsdjasd@gmail.com', 'Ana', 'Lopez', 'https://example.com/ana.jpg', '1990-12-10');


INSERT INTO grupo (id, nombre, descripciton, creador_id) VALUES
    ('d2f3c10e-bb4d-4b6e-8c1f-987654321abc', 'Grupo de prueba', 'Un grupo de ejemplo con dos usuarios', 'a3f1c7c0-d96f-4b4f-8f84-123456789abc');

INSERT INTO grupo_user (grupo_id, user_id) VALUES
                                               ('d2f3c10e-bb4d-4b6e-8c1f-987654321abc', 'a3f1c7c0-d96f-4b4f-8f84-123456789abc'),
                                               ('d2f3c10e-bb4d-4b6e-8c1f-987654321abc', 'b7e2d1a3-8bcb-4e5a-bfd4-987654321def');
