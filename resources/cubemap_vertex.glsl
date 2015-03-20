#version 150 core

in vec3 position;
in vec3 color;
in vec2 texcoord;
in vec3 normal;

out vec3 vertexColor;
out vec3 textureCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vertexColor = color;
    textureCoord = normal;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
}
