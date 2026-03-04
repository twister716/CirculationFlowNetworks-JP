#version 150

in vec3 inPosition;

uniform mat4 u_ModelViewMatrix;
uniform mat4 u_ProjectionMatrix;

void main() {
    gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(inPosition, 1.0);
}
