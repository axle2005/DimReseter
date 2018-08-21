package com.axle2005.dimreseter.sponge.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.axle2005.dimreseter.sponge.Config;



public class Register {

	public Register(com.axle2005.dimreseter.sponge.DimReseter plugin, Config config) {
		CommandSpec add = CommandSpec.builder().permission("dimreset.manage.add")
				.description(Text.of("Add Dim to Config"))
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("Dim|Here"))))
				.executor(new CommandDimAdd(plugin, config)).build();

		CommandSpec dimreset = CommandSpec.builder().description(Text.of("ClearMob Command")).child(add, "add")

				.build();

		Sponge.getCommandManager().register(plugin, dimreset, "DimReseter");
	}
}