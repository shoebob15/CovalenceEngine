#version 330 core
out vec4 FragColor;

in vec3 vColor;
in vec2 vUV;

uniform sampler2D ourTexture;

void main()
{
    FragColor = texture(ourTexture, vUV);
}
