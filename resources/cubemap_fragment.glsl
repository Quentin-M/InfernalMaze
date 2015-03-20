#version 330 core

in vec3 vertexColor;
in vec3 textureCoord;

layout(location = 0) out vec4 fragColor;

uniform samplerCube texImage;

void main() {
    vec4 textureColor = texture(texImage, textureCoord);
    fragColor = vec4(vertexColor, 1.0) * textureColor;
}
