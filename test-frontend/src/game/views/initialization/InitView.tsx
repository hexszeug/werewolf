import React from 'react';
import Players, { PlayerListType } from '../../players/Players';

type PropsType = {
	players: PlayerListType;
};
type StateType = {};

class InitView extends React.Component<PropsType, StateType> {
	constructor(props: PropsType) {
		super(props);
	}

	render() {
		return <Players players={this.props.players} />;
	}
}

export default InitView;
